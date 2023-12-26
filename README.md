# Pancake

[![Release](https://jitpack.io/v/eskalon/pancake.svg)](https://jitpack.io/#eskalon/pancake) [![Build](https://img.shields.io/github/actions/workflow/status/eskalon/pancake/build-and-test.yml?label=Build)](https://github.com/eskalon/pancake/actions)

Pancake is the basis for all libGDX games developed by eskalon.

## Technical 

Pancake uses the following libraries and frameworks :

- [libGDX](https://github.com/libgdx/libgdx)
- [libgdx-screenmanager](https://github.com/crykn/libgdx-screenmanager)
- [guacamole](https://github.com/crykn/guacamole)
- [gdx-vfx](https://github.com/crykn/gdx-vfx) (in a forked version)
- [freetype-skin](https://github.com/crykn/freetype-skin)
- [reflections](https://github.com/ronmamo/reflections) (for `DesktopFieldAnnotationScanner` which is used in for asset loading)

## Content
A selection of what Pancake offers:

### <u>core</u>
- **AbstractEskalonApplication:** the core application; has to be created via `EskalonApplicationStarter` to take advantage of its features. Dependency injection is used to provide various useful objects, for instance a sprite batch, an asset manager, a sound manager, an event bus, a post processing pipeline etc.
- **AnnotationAssetManager**
	- `@Asset("cool_texture.jpg")`, `@Asset(value = "ui/skin/skin.json", params = "ui/skin/skin.atlas")`
	- `#loadAnnotatedAssets(Class<T> clazz)`
	- `#injectAssets(Class<T> clazz, @Nullable T instance)`; superseded by `IInjector#injectMembers(Object)`
	- `#registerAssetLoaderParametersFactory(Class<T> clazz, AssetLoaderParametersFactory<T> factory)`
- **DefaultSoundManager & Playlist**
	- `#playSoundEffect(String name)`
	- `#playMusic(String playlistName)`
	- Playlist files (loaded via `PlaylistDefinitionLoader`):
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
- **EskalonInjector:** a simple dependency injection system; the most common classes are registered by `EskalonApplicationStarter` and `AbstractEskalonApplication`
- **EventBus:** a simple event bus system
- **PostProcessingPipeline:** A simple post processing pipeline based on [gdx-vfx](https://github.com/crykn/gdx-vfx)
- **DefaultInputHandler**; provides mappable key bindings which can be handled via `IInputListener`
- **Lang, ILocalizable & ILocalized**
	- `Lang#get(String key, Object... args)`
- **DebugInfoRenderer:** renders some debug information, including a neat FPS graph
- **AbstractAssetLoadingScreen:** a screen loading all assets annotated with `@Asset` in a specified package 
- **AbstractEskalonUIScreen:** a screen rendering a background image & a stage
- **AbstractImageScreen:** a simple screen rendering one image
- **EskalonSplashScreen:** a splash screen showing the eskalon logo as well as loading some internal assets
- **EskalonSettings:** handles settings via simple property objects
- **RandomUtils:** contains various methods dealing with random values
- **GL32CMacIssueHandler:** provides updated versions of the default shaders that are compatible with OpenGL 3.2

### lwjgl3
- Audio implementations that support spatial audio
- A field annotation scanner utilising [reflections](https://github.com/ronmamo/reflections)

### g3d
- A **deferred renderer** [WIP]


## Collaboration
A list of all of our current contributors and the used external assets can be found in [CONTRIBUTORS.md](https://github.com/eskalon/pancake/blob/master/CONTRIBUTORS.md).
