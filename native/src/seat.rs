use crate::{WLCState, get_time};
use std::sync::{Arc, Mutex};
use std::ops::DerefMut;
use smithay::{
    utils::SERIAL_COUNTER,
    reexports::{
        wayland_server::{
            backend::{ClientId},
            protocol::{
                wl_surface::WlSurface,
                wl_seat::{self, WlSeat},
                wl_pointer::{self, WlPointer, ButtonState, Axis},
            },
            DisplayHandle, Client, GlobalDispatch, Dispatch, New, DataInit,
            Resource,
        },
    },
};

pub struct WLCSeatState {
    pub pointers: Vec<WlPointer>,
}

#[derive(Default)]
pub struct WLCPointerData {
    // WlSurface holding pointer focus
    // This surface has to be of the same client as the WlPointer
    focus: Option<WlSurface>,
}

type WLCPointer = Arc<Mutex<WLCPointerData>>;

fn with_pointer_data<F>(pointer: &WlPointer, f: F)
    where F: FnOnce(&mut WLCPointerData)
{
    let mut guard = pointer
        .data::<WLCPointer>()
        .unwrap()
        .lock()
        .unwrap();
    let data = guard.deref_mut();
    f(data);
}

fn new_serial() -> u32 {
    SERIAL_COUNTER.next_serial().into()
}

impl WLCSeatState {
    pub fn new() -> Self {
        WLCSeatState {
            pointers: vec![],
        }
    }

    pub fn create_global(&self, disp: &DisplayHandle) {
        disp.create_global::<WLCState, WlSeat, ()>(10, ());
    }

    // Set pointer focus on a surface and register movement
    pub fn pointer_motion(&self, surface: WlSurface, x: f64, y: f64) {
        if !surface.is_alive() { return };
        let client = surface.client().unwrap();

        for pointer in &self.pointers {
            let mut guard = pointer
                .data::<WLCPointer>()
                .unwrap()
                .lock()
                .unwrap();
            let data = guard.deref_mut();
            let pointer_client = pointer.client().unwrap();

            // If WlPointer belongs to different client, make it lose focus
            if pointer_client != client {
                if let Some(focus) = &data.focus {
                    pointer.leave(new_serial(), focus);
                    pointer.frame();
                    data.focus = None;
                }
                continue;
            }

            // This pointer is now guaranteed to be of the same client as the
            // surface

            if let Some(focus) = &data.focus {
                if *focus != surface {
                    // Previously focusing different surface
                    pointer.leave(new_serial(), focus);
                    pointer.enter(new_serial(), &surface, x, y);
                    data.focus = Some(surface.clone());
                } else {
                    // Focus already on this surface
                    pointer.motion(get_time(), x, y);
                }
                pointer.frame();
            } else {
                pointer.enter(new_serial(), &surface, x, y);
                pointer.frame();
                data.focus = Some(surface.clone());
            }
        }
    }

    pub fn pointer_button(&self, button: u32, state: ButtonState) {
        self.for_all_pointers(|pointer, data| {
            if data.focus.is_some() {
                pointer.button(new_serial(), get_time(), button, state);
                pointer.frame();
            }
        });
    }

    pub fn pointer_axis(&self, axis: Axis, value: f64) {
        self.for_all_pointers(|pointer, data| {
            if data.focus.is_some() {
                pointer.axis(get_time(), axis, value);
                pointer.frame();
            }
        });
    }

    // Remove pointer from any surfaces
    pub fn pointer_unfocus(&self) {
        self.for_all_pointers(|pointer, data| {
            if let Some(focus) = &data.focus {
                pointer.leave(new_serial(), focus);
                pointer.frame();
                data.focus = None;
            }
        });
    }

    fn for_all_pointers<F>(&self, mut f: F)
        where F: FnMut(&WlPointer, &mut WLCPointerData)
    {
        for pointer in &self.pointers {
            with_pointer_data(pointer, |data| f(pointer, data));
        }
    }
}

impl GlobalDispatch<WlSeat, ()> for WLCState {
    fn bind(
        _state: &mut Self,
        _handle: &DisplayHandle,
        _client: &Client,
        resource: New<WlSeat>,
        _data: &(),
        data_init: &mut DataInit<'_, Self>,
    ) {
        let seat: WlSeat = data_init.init(resource, ());
        seat.name("seat-0".into());

        let mut caps: wl_seat::Capability = wl_seat::Capability::empty();
        caps.insert(wl_seat::Capability::Pointer);
        seat.capabilities(caps);
    }
}

impl Dispatch<WlSeat, ()> for WLCState {
    fn request(
        state: &mut Self,
        _client: &Client,
        seat_resource: &WlSeat,
        request: wl_seat::Request,
        _data: &(),
        _disp: &DisplayHandle,
        data_init: &mut DataInit<'_, Self>,
    ) {
        match request {
            wl_seat::Request::GetPointer { id } => {
                let pointer_data = WLCPointerData {
                    focus: None,
                };
                let pointer = Arc::new(Mutex::new(pointer_data));

                let pointer_resource: WlPointer =
                    data_init.init(id, pointer.clone());

                state.seat.pointers.push(pointer_resource);
            },
            _ => {
                seat_resource.post_error(
                    wl_seat::Error::MissingCapability,
                    "accessed missing seat capability",
                );
            },
        }
    }
}

impl Dispatch<WlPointer, WLCPointer> for WLCState {
    fn request(
        _state: &mut Self,
        _client: &Client,
        _pointer_resource: &WlPointer,
        request: wl_pointer::Request,
        _data: &WLCPointer,
        _disp: &DisplayHandle,
        _data_init: &mut DataInit<'_, Self>,
    ) {
        match request {
            wl_pointer::Request::SetCursor { .. } => {},
            wl_pointer::Request::Release => {},
            _ => unreachable!(),
        }
    }

    fn destroyed(
        state: &mut Self,
        _client: ClientId,
        pointer_resource: &WlPointer,
        _data: &WLCPointer,
    ) {
        state.seat.pointers.retain(|p| p != pointer_resource);
    }
}
