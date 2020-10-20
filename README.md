# Pancake

[![Release](https://jitpack.io/v/eskalon/pancake.svg)](https://jitpack.io/#eskalon/pancake) [![Build Status](https://travis-ci.com/eskalon/pancake.svg?branch=master)](https://travis-ci.com/eskalon/pancake)

Pancake is the basis for all libGDX games developed by eskalon.

## Technical 

Pancake uses the following libraries and frameworks :

- [libGDX](https://github.com/libgdx/libgdx)
- [libgdx-screenmanager](https://github.com/crykn/libgdx-screenmanager)
- [guacamole](https://github.com/crykn/guacamole)
- [guava-eventbus](https://github.com/crykn/guava-eventbus) and [reflections](https://github.com/ronmamo/reflections) (for the classpath scanning in AbstractAssetLoadingScreen)

## Content
A selection of what Pancake offers:

### <u>core:</u>
- **AnnotationAssetManager**
	- `@Asset("cool_texture.jpg")`, `@Asset(value = "ui/skin/skin.json", params = "ui/skin/skin.atlas")`
	- `#loadAnnotatedAssets(Class<T> clazz)`
	- `#injectAssets(Class<T> clazz, @Nullable T instance)`
	- `#registerAssetLoaderParametersFactory(Class<T> clazz, AssetLoaderParametersFactory<T> factory)`
- **DefaultSoundManager & Playlist**
	- `#playSoundEffect(String name)`
	- `#playMusic(String playlistName)`
	- Playlist files:
```java
{
   name: "best_playlist_ever",
   shuffle: true,
   repeat: true,
   music: [
        [
           "My favourite song",
           "my_favourite_song.mp3"
        ],
     ]
 }
 ```
- **EskalonApplication:** the core application, which keeps a sprite batch, an asset manager, a sound manager, an event bus etc.
- **PostProcessingPipeline:** A simple post processing pipeline [WIP]
- **Lang, ILocalizable & ILocalized**
	- `Lang#get(String key, Object... args)`
- **DebugInfoRenderer:** renders some debug information, including a fps graph
- **AbstractAssetLoadingScreen:** a screen loading all assets annotated with `@Asset` in a specified package 
- **AbstractEskalonUIScreen:** a screen rendering a background image & a stage
- **AbstractImageScreen:** a simple screen rendering one image
- **EskalonSplashScreen:** a splash screen showing the eskalon logo as well as loading some internal assets
- **EskalonSettings & KeyBinding:** provides settings for different sound volumes & makes handling changeable key bindings very simple
- **RandomUtils:** contains various methods dealing with random values
- **GL32CMacIssueHandler:** provides updated versions of the default shaders that are compatible with OpenGL 3.2

### desktop
- Audio implementations that support spatial audio

### g3d:
- A **deferred renderer** [WIP]


## Collaboration
A list of all of our current contributors and the used external assets can be found in [CONTRIBUTORS.md](https://github.com/eskalon/pancake/blob/master/CONTRIBUTORS.md).
