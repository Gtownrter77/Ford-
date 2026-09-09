import type { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "com.gurustudios.sporttrac",
  appName: "Sport Trac Mentor",
  webDir: "android-www",
  backgroundColor: "#0c0d0b",
  android: {
    allowMixedContent: true,
    webContentsDebuggingEnabled: true,
  },
};

export default config;
