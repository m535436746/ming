package com.telstar.launcher.input.bean;


public class HdmiInputEntity extends InputBase {
	
	public static final int SOURCE_HDMI1 = 0;
	public static final int SOURCE_HDMI2 = 1;
	public static final int SOURCE_HDMI3 = 2;
	public static final int SOURCE_HDMI4 = 3;
	
	public static final int TVIN_PORT_HDMI0 = 0x00004000;
	public static final int TVIN_PORT_HDMI1 = 0x00004001;
	public static final int TVIN_PORT_HDMI2 = 0x00004002;
	public static final int TVIN_PORT_HDMI3 = 0x00004003;
	public static final int TVIN_PORT_HDMI4 = 0x00004004;
	public static final int TVIN_PORT_HDMI5 = 0x00004005;
	public static final int TVIN_PORT_HDMI6 = 0x00004006;
	public static final int TVIN_PORT_HDMI7 = 0x00004007;
	
	public static final int[] SOURCE_HDMI_IN = {
			SOURCE_HDMI1,
			SOURCE_HDMI2,
			SOURCE_HDMI3,
			SOURCE_HDMI4,
	};
	
    public static final InputMode[] INPUTMODES = new InputMode[] {
            new InputMode("640x480p60", false, 640, 480, 60),
            new InputMode("480p", false, 720, 480, 60),
            new InputMode("720p", false, 1280, 720, 60),
            new InputMode("1080i", true, 1920, 1080, 60),
            new InputMode("480i", true, 720, 480, 60),
            new InputMode("240p", false, 720, 240, 60),
            new InputMode("1080p", false, 1920, 1080, 60),
            new InputMode("576p", false, 720, 576, 50),
            new InputMode("576p60hz", false, 720, 576, 60),
            new InputMode("720p50hz", false, 1280, 720, 50),
            new InputMode("1080i50hz", true, 1920, 1080, 50),
            new InputMode("288p", false, 720, 240, 50),
            new InputMode("576i", true, 720, 576, 50),
            new InputMode("1080p50hz", false, 1920, 1080, 50),
            new InputMode("1080p24hz", false, 1920, 1080, 24),
            new InputMode("1080p25hz", false, 1920, 1080, 25),
            new InputMode("1080p30hz", false, 1920, 1080, 30),
            new InputMode("1080i100hz", true, 1920, 1080, 100),
            new InputMode("720p100hz", false, 1280, 720, 100),
            new InputMode("576p100hz", false, 720, 576, 100),
            new InputMode("576i100hz", true, 720, 576, 100),
            new InputMode("1080i120hz", true, 1920, 1080, 120),
            new InputMode("720p120hz", false, 1280, 720, 120),
            new InputMode("480p120hz", false, 720, 480, 120),
            new InputMode("480i120hz", true, 720, 480, 120),
            new InputMode("576p200hz", false, 720, 576, 200),
            new InputMode("576i200hz", true, 720, 576, 200),
            new InputMode("480p240hz", false, 720, 480, 240),
            new InputMode("480i240hz", true, 720, 480, 240),
            new InputMode("720p24hz", false, 1280, 720, 24),
            new InputMode("720p25hz", false, 1280, 720, 25),
            new InputMode("720p30hz", false, 1280, 720, 30),
            new InputMode("1080p120hz", false, 1920, 1080, 120),
            new InputMode("1080p100hz", false, 1920, 1080, 100),
            new InputMode("800x600", false, 800, 600, 0),
            new InputMode("1024x768", false, 1024, 768, 0),
            new InputMode("720x400", false, 720, 400, 0),
            new InputMode("1280x768", false, 1280, 768, 0),
            new InputMode("1280x800", false, 1280, 800, 0),
            new InputMode("1280x960", false, 1280, 960, 0),
            new InputMode("1280x1024", false, 1280, 1024, 0),
            new InputMode("1360x768", false, 1360, 768, 0),
            new InputMode("1366x768", false, 1366, 768, 0),
            new InputMode("1600x1200", false, 1600, 1200, 0),
            new InputMode("1920x1200", false, 1920, 1200, 0),
            new InputMode("1440x900", false, 1440, 900, 0),
            new InputMode("1400x1050", false, 1400, 1050, 0),
            new InputMode("1680x1050", false, 1680, 1050, 0),
            new InputMode("3840x2160", false, 3840, 2160, 0),
            new InputMode("4096x2160", false, 4096, 2160, 0),
            new InputMode("480i60hz", true, 720, 480, 60),
            new InputMode("576i50hz", true, 720, 576, 50),
            new InputMode("800x600p60", false, 800, 600, 60),
            new InputMode("1024x768p60", false, 1024, 768, 60),
            new InputMode("1152x864p60", false, 1152, 864, 60),
            new InputMode("1280x768p60", false, 1280, 768, 60),
            new InputMode("1280x800p60", false, 1280, 800, 60),
            new InputMode("1280x960p60", false, 1280, 960, 60),
            new InputMode("1280x1024p60", false, 1280, 1024, 60),
            new InputMode("1366x768p60", false, 1366, 768, 60),
            new InputMode("1600x1200p60", false, 1600, 1200, 60)
        };

	public static final int INPUT_SOURCE_HDMIIN_SII9293 = 0;
	public static final int INPUT_SOURCE_HDMIIN_SII9233 = 1;
	public static final int INPUT_SOURCE_HDMIIN_INTERNAL = 2;
	public static final int INPUT_SOURCE_VGA = 3;
	public static final int INPUT_SOURCE_CVBS = 4;

	private int mInputSource = TVIN_PORT_HDMI0;
	private int mInputSourceType = INPUT_SOURCE_HDMIIN_INTERNAL;

	public HdmiInputEntity(String name, InputType type, int state, int inputSource, int inputSourceType) {
		super(name, type, state);
		// TODO Auto-generated constructor stub
		mInputSource = inputSource;
		mInputSourceType = inputSourceType;
	}

	public int getInputSource() {
		return mInputSource;
	}

	public void setInputSource(int mInputSource) {
		this.mInputSource = mInputSource;
	}

	public int getInputSourceType() {
		return mInputSourceType;
	}

	public void setInputSourceType(int mInputSourceType) {
		this.mInputSourceType = mInputSourceType;
	}

}
