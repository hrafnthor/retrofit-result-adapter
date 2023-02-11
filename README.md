# retrofit-result-adapter

[![License](https://img.shields.io/badge/License-MIT-blue)](https://choosealicense.com/licenses/mit/)
[![Maven Central](https://img.shields.io/maven-central/v/is.hth/retrofit-result-adapter?color=blue)](https://central.sonatype.com/artifact/is.hth/retrofit-result-adapter/0.2.0)


This is a small library that wraps Retrofit responses in result monads, specifically [kotlin-result](https://github.com/michaelbull/kotlin-result) monads.

When constructing a Retrofit instance, simply add an instance of `ResultCallAdapterFactory` as a `CallAdapterFactory` to the builder:

```kotlin
Retrofit.Builder()
.addCallAdapterFactory(ResultCallAdapterFactory(processor = ErrorProcessor<TheErrorWrapperType>()))
.build()

```

This `Retrofit` instance can now process api interfaces defined as so:

```kotlin
interface RetrofitApi {

@GET("SOME_URI")
suspend fun getSomeData(): Result<TheResultType, TheErrorWrapperType>
}
```

And the call site simply looks like this:

```kotlin
val api = Retrofit.create(RetrofitApi::class.java)

api
.getSomeData()
.onSuccess {
    // response to success
}
.onFailure {
    // respond to failure or skip doing so here at the call site,
    // rather passing the result monad higher up the chain for the
    // error processing
}
```

The `ResultCallAdapterFactory` will pass through any api definitions that are not using a Result monad, not touching them, as a `CallAdapterFactory` should do.

For instance there will not be any issues in using the following api definition:

```kotlin
interface MixedRetrofitApi {

@GET("SOME_URI")
suspend fun getSomeData(): Result<TheResultType, TheErrorWrapperType>

suspend fun someOtherData(): TheResultType
}
```

##### ErrorProcessor

As can be seen, there is something called an `ErrorProcessor`, that gets handed off to the `ResultCallAdapterFactory`, which has a generic type `TheErrorWrapperType` that is the same as for the endpoint `getSomeData()` defined in the api interface above.

The `ErrorProcessor` is an interface which gets invoked when errors occur within the `ResultCallAdapterFactory`, or within the processing chain it leads to. This allows for the return of custom errors under different situations and pushes that determination inside the networking layer, making the network call site a bit cleaner.

For instance, lets imagine that there exists a unified type for error delivery in a project and that it is called `Cause`, and that it breaks the error down based on layer with the option of having each layer implement their own detailed implementation along with specific return types:

```kotlin
sealed interface Cause {

val msg: String

class Unknown(override val msg: String) : Cause

interface Network : Cause

interface Storage: Cause

interface Domain : Cause
}
```

With that there is then a `NetworkCause` definition inside the network layer like so:

```kotlin
sealed class NetworkCause(override val msg: String): Cause.Network {

object Empty: NetworkCause("Empty response received")

class Exception(error: Throwable): NetworkCause(msg = error.message ?: "")

class NetworkError(val code: Int, message: String): NetworkCause(msg = message)
}
```

In it's simplest form, the `ErrorProcessor` for the `ResultCallAdapterFactory` can now look like this when returning a `Cause` as the error delivery object:

```kotlin
val processor = object:ErrorProcessor<Cause> {
override fun onEmpty(): Cause = NetworkCause.Empty

override fun onException(error: Throwable): Cause = NetworkCause.Exception(error)

override fun onNetworkError(code: Int, errorBody: ResponseBody?): Cause = NetworkCause.NetworkError(code, someProcessingFunction(errorBody))

override fun onUnknown(detail: String): Cause = Cause.Unknown(detail)
}
```

##### Nullable response body

If an endpoint has the potential to return a null response body the `ResultCallAdapterFactory` requires the endpoint to be annotated with its own `@NullableBody` annoation.

Consider the api definition below:

```kotlin
interface RetrofitApi {

@NullableBody
@GET("SOME_URI")
suspend fun thisMightBeNull(): Result<TheResultType, TheErrorWrapperType>

@GET("SOME_URI")
suspend fun thisReturnsNothing(): Result<Unit, TheErrorWrapperType>

@GET("SOME_URI")
suspend fun thisShouldReturnValues(): Result<TheResultType, TheErrorWrapperType>
}
```

For this interface the following behaviour will occur:

- `thisMightBeNull` will either return a `Ok(TheResultType)` or `Ok(null)` in case of a successful network call, depending on if the response body is empty or not

- `thisReturnNothing` expects to return nothing and will return `Ok(Unit)` in the case of a successful network call and a empty body.

- `thisShouldReturnValues` will return a `Err(processor.onEmpty())` in the case of a successful network call with a empty body


#### Download

```
repositories {
  mavenCentral()
}

dependencies {
  implementation 'is.hth:retrofit-result-adapter:0.2.0'
}
```

<details>
<summary>Snapshot from the latest development version are available at Sonatype's snapshot repository</summary>
<p>

```
repositories {
  mavenCentral()
  maven {
    url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
  }
}

dependencies {
  implementation 'is.hth:retrofit-result-adapter:0.3.0-SNAPSHOT'
}
```

</p>
</details>


License
-------

    Copyright (c) 2022 Hrafn Þorvaldsson

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
