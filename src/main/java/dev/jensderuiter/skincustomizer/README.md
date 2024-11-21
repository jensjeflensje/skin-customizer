## Creating Skin Customizers using Java
You can instantiate the [`SkinCustomizer`](customizer/SkinCustomizer.java)
class to create a customizer.

**Note: customizers are not persistent! Once the server restarts, they're gone.
So make sure your integration re-creates them if you want them to be persistent!**

Example code to create a customizer:
```java
Location location = ...;
SkinCustomizer customizer = new SkinCustomizer(location);
customizer.summon(); // this will actually summon the preview and UI
```