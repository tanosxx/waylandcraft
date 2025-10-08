use std::ffi::{CStr, CString};

pub type EGLBoolean = libc::c_uint;
pub type EGLDisplay = *mut libc::c_void;
pub type EGLDeviceEXT = *mut libc::c_void;
pub type EGLint = i32;
pub type EGLAttrib = libc::intptr_t;

pub const EGL_DEVICE_EXT: EGLint = 0x322C;
pub const EGL_DRM_RENDER_NODE_FILE_EXT: EGLint = 0x3377;

type ProcAddrFn = extern "C" fn(*const libc::c_char) -> extern "C" fn();

#[allow(non_snake_case)]
pub struct EGLHelper {
    pub display: EGLDisplay,
    eglQueryDisplayAttribEXT:
        extern "C" fn(EGLDisplay, EGLint, *mut EGLAttrib) -> EGLBoolean,
    eglQueryDeviceStringEXT:
        extern "C" fn(EGLDeviceEXT, EGLint) -> *const libc::c_char,
}

impl EGLHelper {
    #[allow(non_snake_case)]
    pub fn new(dpy: EGLDisplay, proc_addr_ptr: usize) -> Self {
        let glfwGetProcAddress: ProcAddrFn = unsafe {
            std::mem::transmute(proc_addr_ptr)
        };

        macro_rules! getfn {
            ($name:ident) => {
                let $name = {
                    let n = CString::new(stringify!($name)).unwrap();
                    unsafe {std::mem::transmute(
                        glfwGetProcAddress(n.as_c_str().as_ptr())
                    )}
                };
            };
        }

        getfn!(eglQueryDisplayAttribEXT);
        getfn!(eglQueryDeviceStringEXT);

        Self {
            display: dpy,
            eglQueryDisplayAttribEXT,
            eglQueryDeviceStringEXT,
        }
    }

    pub fn get_render_node(&self) -> &'static str {
        let mut dev_ret: EGLAttrib = 0;

        (self.eglQueryDisplayAttribEXT)(
            self.display,
            EGL_DEVICE_EXT,
            &mut dev_ret
        );

        let dev: EGLDeviceEXT = (dev_ret as usize) as EGLDeviceEXT;

        let name_ptr = (self.eglQueryDeviceStringEXT)(
            dev,
            EGL_DRM_RENDER_NODE_FILE_EXT
        );

        unsafe { CStr::from_ptr(name_ptr).to_str().unwrap() }
    }
}
