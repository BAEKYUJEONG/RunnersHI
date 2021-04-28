# HttpConnect 클래스 가이드

## 1. 객체 선언

선언 및 할당 방법엔 2가지가 있습니다.

```kotlin
var 변수이름:HttpConnect = HttpConnect(urlAddress:String)
var 변수이름:HttpConnect = HttpConnect(urlAddress:String, params:Map<String, Any>)
```

위의 경우는 url만 있으면 될때, 아래의 경우는 url과 parameters가 필요할때 입니다.

**URL로 들어가는 String은 무조건 `/` 로 시작합니다.**

### 활용 예시

#### 1. URL만 활용

```kotlin
// 타입 지정인 :HttpConnect는 생략가능합니다.
var hconn = HttpConnect("/getId/1")
```

#### 2. URL과 Parameters

```kotlin
// Parameter Map을 우선 만들어야합니다.
// Map을 만드는데에는 여러 방법이 있으나 지금은 1개만 사용해서 예를 들겠습니다.
var paramMap = mapOf("userId" to 3)
// 위의 경우 "userId":3 이 만들어졌다고 보면됩니다.
var hconn = HttpConnect("/postId", paramMap)
```



## 2. 함수 지정

> HttpConnect 클래스는 4개의 함수를 활용합니다.

#### .get() : return String

#### .post() : return String

#### .delete() : return String

#### .put() : return String

모든 함수는 String을 반환합니다. 추후 필요에 따라 Json array 나 Json object로 변환하여 사용하면 됩니다.

### 활용 예시

```kotlin
// Parameter가 있으면 넣고 없으면 안넣으면 됩니다.
var hget = HttpConnect("/getId/1")
var hpost = HttpConnect("/postId", paramMap)
var hdelete = HttpConnect("/delete/1")
var hput = HttpConnect("/put", paramMap)

var getResult:String = hget.get()
var postResult:String = hget.post()
var deleteResult:String = hget.delete()
var putResult:String = hget.put()

//----------------------------------------------------------------//

// 객체의 선언없이 1줄로도 가능합니다.
var getResult:String = HttpConnect("/getId/1").get()
var postResult:String = HttpConnect("/postId", paramMap).post()
var deleteResult:String = HttpConnect("/delete/1").delete()
var putResult:String = HttpConnect("/put", paramMap).put()
```



현재 단편적인 테스트만 진행했기에 에러 데이터 수집이 미흡합니다.

에러가 발생하는 즉시 알려주시면 감사하겠습니다.

또한 현재 제가 구현한 통신은 과거에 제가 했던 방식을 차용했을뿐 Android에서 권장하는 방법은 아닙니다.

권장하는 방법을 사용하기 위해선 MVVM 패턴을 정확히 구현해야할텐데 그러면 시간이 상당히 소요될듯합니다.


