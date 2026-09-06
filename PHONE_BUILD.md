# ساخت APK با گوشی

## روش پیشنهادی: GitHub Actions

1. فایل ZIP پروژه را Extract کنید.
2. کل محتویات پوشه `V2TunClient` را در مخزن GitHub خود با نام `V2rayTun0` قرار دهید.
3. در GitHub وارد تب **Actions** شوید.
4. Workflow با نام **Build V2TunClient APK** را انتخاب کنید.
5. روی **Run workflow** بزنید.
6. پس از پایان Build، از بخش **Artifacts** فایل `V2TunClient-debug-apk` را دانلود کنید.

این روش به Android Studio روی گوشی نیاز ندارد.
