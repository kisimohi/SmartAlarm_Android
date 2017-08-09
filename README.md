# Smart Alarm Android App

This is a smart alarm application for Android and Thing Interaction Framework (Kii Cloud).

See author's personal [blog page](http://blog.kissy-software.com/) (Japanese) for the detail.

## How to build

* Configure FCM and add google-services.json to the project.
* Replace the symbols in KiiAPI.java

    ```java
    public static final String APP_ID = "<<your app id>>";
    public static final String APP_KEY = "<<your app key>>";
    public static final Kii.Site APP_SITE_CLOUD = Kii.Site.JP;
    public static final Site APP_SITE_THING = Site.JP;
    ```

## Notes

* This app is made by Android Studio 2.2.
* You need to setup the IoT device side. See [GitHub page](https://github.com/kisimohi/SmartAlarm_IoTDevice)
* This app supports Japanese national holidays until 2025.

## License

This software is distributed under [MIT License](http://opensource.org/licenses/mit-license.php).
