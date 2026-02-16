use crate::WLCState;
use smithay::{
    utils::{Size, Physical},
    reexports::{
        wayland_server::{
            protocol::{
                wl_output::{self, WlOutput},
            },
            DisplayHandle, DataInit, New, GlobalDispatch, Dispatch, Client,
            Resource,
        },
    },
};

pub struct WLCOutput {
    pub outputs: Vec<WlOutput>,
    size: Size<i32, Physical>,
    display_handle: DisplayHandle,
}

impl WLCOutput {
    pub fn new(display_handle: &DisplayHandle) -> Self {
        WLCOutput {
            outputs: vec![],
            size: Size::new(1920, 1080),
            display_handle: display_handle.clone(),
        }
    }

    pub fn create_global(&self) {
        self.display_handle.create_global::<WLCState, WlOutput, ()>(4, ());
    }

    pub fn width(&self) -> i32 {
        self.size.w
    }

    pub fn height(&self) -> i32 {
        self.size.h
    }

    pub fn resize(&mut self, width: i32, height: i32) {
        self.size = Size::new(width, height);
        let flags = wl_output::Mode::Current;
        for output in &self.outputs {
            output.mode(flags, self.size.w, self.size.h, 0);
            if output.version() >= 2 {
                output.done();
            }
        }
    }
}

impl GlobalDispatch<WlOutput, ()> for WLCState {
    fn bind(
        state: &mut Self,
        _handle: &DisplayHandle,
        _client: &Client,
        resource: New<WlOutput>,
        _data: &(),
        data_init: &mut DataInit<'_, Self>,
    ) {
        let output: WlOutput = data_init.init(resource, ());

        let flags = wl_output::Mode::Current;
        let size = &state.output.size;
        output.mode(flags, size.w, size.h, 0);

        output.geometry(
            0, 0, // location
            0, 0, // physical dimensions
            wl_output::Subpixel::None, // subpixel
            "Virtual".into(), // make
            "Monitor".into(), // model
            wl_output::Transform::Normal, // transform
        );

        if output.version() >= 4 {
            output.name("output-0".into());
            output.description("Virtual Output".into());
        }

        if output.version() >= 2 {
            output.scale(1);
            output.done();
        }
    }
}

impl Dispatch<WlOutput, ()> for WLCState {
    fn request(
        _state: &mut Self,
        _client: &Client,
        _output: &WlOutput,
        request: wl_output::Request,
        _data: &(),
        _disp: &DisplayHandle,
        _data_init: &mut DataInit<'_, Self>,
    ) {
        match request {
            wl_output::Request::Release => {},
            _ => unreachable!(),
        }
    }
}
