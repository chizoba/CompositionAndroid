# CompositionAndroid
An example of how to implement composition on android components (Activity/Fragment) without any third-party library.
This also gives your delegates an extra superpower of knowing the lifecycle state of its `LifecycleOwner`.

**NB:** In this post, 
* I used composition and delegation interchangeably i.e. `composition == delegation`.
* LifecycleOwner == Activities/Fragments or your custom LifecycleOwner.

---


## Background
As we need to reuse logic in our Activities/Fragments, we sometimes tend to have a deep inheritance hierarchy of which not all 
inherited methods maybe needed and which leads to what is called "Inheritance Hell". This makes code difficult to read, 
navigate, and maintain. To solve this, composition is preferred over inheritance.


## How does this work?
As the description of this example project says, we are not making use of any third-party library but rather we would be using 
Lifecycle-Aware Components that is part of AndroidX.

### Step 1: 
Add the lifecycle-aware dependency.
```gradle
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0'
```

### Step 2:
Create your delegate.
```kotlin
class ScreenshotSecurityDelegate : LifecycleObserver {
    init {
        // One place to add this observer to the calling LifecycleOwner. More on this below.
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        // Do what ever you want to happen at `onResume()` of Activity/Fragment.
        disableScreenshotUsage()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        // Do what ever you want to happen at `onStop()` of Activity/Fragment.
        // Remove this observer from the calling LifecycleOwner.
        ...
    }

    fun enableScreenshotUsage() {
        ...
    }

    fun disableScreenshotUsage() {
        ...
    }

    ...
}

```

As you can see, your delegate is a `LifecycleObserver`, which means it can respond to lifecycle events that happen in any
`LifecycleOwner` that adds it to its list of observers.

List of other lifecycle events can be found [here](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.Event).

### Step 3:
Register your delegate to receive lifecycle events. You can do this either in your:
1. LifecycleOwner:
```kotlin
class MainActivity : AppCompatActivity() {
    private val screenshotSecurityDelegate = ScreenshotSecurityDelegate()

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         lifecycle.addObserver(screenshotSecurityDelegate)

         ...
     }

    ...
}
```
2. Delegate (this is preferably so as to ensure separation of concerns):
```kotlin
class ScreenshotSecurityDelegate(activity: AppCompatActivity) : LifecycleObserver {
    init {
        activity.lifecycle.addObserver(this)
    }
    
    ...
}
```


Depending on what you need to do inside your delegate (like you see above), you can tend to *or* not to pass the 
`LifecycleOwner` reference, but you need to handle/prevent memory leaks. You can do this by either:

1. Making your `LifecycleOwner` a weak reference (preferable):
```kotlin
class ScreenshotSecurityDelegate(activity: AppCompatActivity) : LifecycleObserver {
    private val lifecycleOwner = WeakReference(activity).get()

    ...
  
    fun disableScreenshotUsage() {
        lifecycleOwner?.run { window.addFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
}
```

OR

2. Setting your `LifecycleOwner` to `null` during an ending lifecycle event (Not later than `ON_STOP`).
```kotlin
class ScreenshotSecurityDelegate(var activity: AppCompatActivity?) : LifecycleObserver {
    fun disableScreenshotUsage() {
        activity?.run { window.addFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
    
    ...
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        activity = null
    }    
}
```


## Its calling order...
* When the `LifecycleOwner` is being **created** - The respective lifecycle methods are called in the `LifecycleOwner` before
that of your delegates. The order in which delegates are called is based on which is declared earliest i.e. If `Delegate1` is
declared before `Delegate2`:
```
Call stack:
Activity/Fragment -> onCreate()
Delegate1         -> onCreate()
Delegate2         -> onCreate()
...
Activity/Fragment -> onResume()
Delegate1         -> onResume()
Delegate2         -> onResume()
```

* When the `LifecycleOwner` is being **destroyed** - The respective lifecycle methods are called in the delegates before 
that of your `LifecycleOwner`. The order in which delegates are called is based on which was declared latest i.e. If `Delegate1`
is declared before `Delegate2`:
```
Call stack:
Delegate2         -> onPause()
Delegate1         -> onPause()
Activity/Fragment -> onPause()
Delegate2         -> onStop()
Delegate1         -> onStop()
Activity/Fragment -> onStop()
...
```


## Some other case scenarios:
* If your delegate depends on a class from dependency injection of which its injection call happens in `onCreate()` of 
your `LifecycleOwner`, you can instantiate your delegate just after the injection call, and all the methods of your delegate 
(annotated with lifecycle events) will still get called.
 
  Basically, what happens is that despite your `LifecycleOwner` maybe in `onStart()` or `onResume()` state, whenever your delegate 
gets added as an observer, its lifecycle methods are called starting from `onCreate()` until it gets to the current state of 
the `LifecycleOwner`.


## Pros:
- Your delegates are now lifecycle aware.
- Only lifecycle methods you need/create are called within your delegates.
- Ease of understanding and maintaining code.
- No dependency on third-party libraries + No need to learn how "yet another library" works.


## Credits:
This [blog post](https://medium.com/@manuelvicnt/composite-views-in-android-composition-over-inheritance-4a7114609560) gave me 
an insight into composition on android components.

To learn more about lifecycle-aware components, see its [documentation](https://developer.android.com/topic/libraries/architecture/lifecycle). 

**PS:** Another very important method of lifecycle-aware components is `isAtLeast()` which you can use to ensure your lifecycle is 
in a good state before calling a function that may led to a crash. More information in the documentation.

**PPS:** Do check out the code sample to see for yourself how it really works, star and share ;)

Thanks for reading :)
