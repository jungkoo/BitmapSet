HyperSet
=========

개발 : 정민철 (deajang@gmail.com)
블로그 : http://blog.indf.net/28


## 1. 개요 ##
중복된 아이템을 제외한 유일한 카운트 UC(UniqueCount)를 구하기 위해서는, Java에서 제공하는 Set을 이용하여 중복된 아이템 갯수을 카운팅 할 수 있다.
엄청나게 많은 아이템의 UC를 제약된 메모리에서 많은 아이템의 UC를 구한다는것은 어려운 문제이다.
HyperSet은 Bitset의 문제점을 보완하여 만들어낸 구현체이다. (허용값은 0보다큰 Integer범위)
아래와 같이 HashSet<Integer>()을 HyperSet()으로 대체 가능하다.


    public static void main(String ...argv) {
        //Set<Integer> set = new HashSet<Integer>();
        HyperSet set = new HyperSet();
        set.add(11);
        set.add(42321);
        set.add(444);
        set.add(11);
        set.add(1234);
        // set ==> [11, 42321, 44, 1234] , size=4
        System.out.println("UC = " + set.size() ); // ==>    UC = 4
    }
 


## 2. 기존 자료구조의 특징 ##
### 2.1 HashSet ###
HashSet의 경우 문자열과 숫자형 모두 사용가능한 장점이 있다. 그리고 아이템의 갯수에 비례하여 메모리가 늘어나는 특징을 지닌다.
Integer데이터를 넣는다면, 256mb의 환경에서는 약 560만개의 아이템을 넣을수 있고, 숫자의 분포나 크기에 상관없이 아이템 갯수에만 영향받는다.

    public static void main(String ...argv){
	    Set<Integer> set = new HashSet<Integer>();		    
	    for(int i=1; i<100000000; i++) {
	   		set.add(i);
	    	if (i%1000000==0)
	    		System.out.println(i);
	    }
	    System.out.println("size?" + set.size());
    }

	//==> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space


### 2.2 BitSet ###
BitSet의 경우 숫자형만 사용가능하며, bit연산자를 이용하기 때문에 메모리를 효율적으로 이용할수 있다.
256mb 환경이라면 1~13억의 숫자의 UC를 계산할 수 있다. 특징은 숫자의 최대값에 영향을 받는다는 점이다.

즉, 1개의 아이템이라도 13억이라는 숫자 하나를 넣는다면, 연속된 bit배열을 만들어야 하는 단점이 존재한다. (13억번째 bit에 1로 set)

    public static void main(String ...argv){
        BitSet set = new BitSet();
        set.set(80000000);
        set.set(90000000);
        set.set(1300000000);
        System.out.println("size? " + set.cardinality());
    }

	//==> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space



## 3. HyperSet 기본아이디어 ##
### 3.1 블럭단위의 메모리 관리 (HyperSet.java) ###
BitSet 자료구조의 장점은 bit연산을 통해 데이터를 관리하여 메모리가 절약할 수 있다는 점이다. 하지만, 연속된 bit배열을 만들어야 하다보니 아이템 갯수가 아닌 최대값(MAX)에 메모리 사용률이 결정받는다는 문제가 있다.

이런 단점을 해결하기위해, "HyperSet" 에서는 논리적으로 N개의 블럭으로 쪼개어 관리한다.

예를 들어,'45', '58', '60'을 넣었을때 차이는 아래와 같다. (숫자는 2진수 bit로 보면된다)

    * BitSet
    [0000000000000000000000000000000000000000000010000000000001010000]   
    
    * HyperSet (블럭사이즈가 10bit 일때)
    4 -- [0000100000]
    5 -- [0000000101]


BitSet은 연속된 데이터이기 때문에 최대값에 가깝게 메모리생성을 되어 있지만,
HyperSet의 경우 '블럭인덱스'와 '블럭' 을 통해 관리된다. 그렇기 때문에 연속된 큰 공간이 필요없다.


### 3.2 가득찬 블럭(FULL)의 메모리 절약 (FixedBitSet.java) ###

블럭단위로 데이터를 관리한다면, 블럭에는 값의 범위가 정해진다.
해당 범위의 값이 꽉차있을때, 범위안의 숫자라면 블럭에 어떤값이 추가(add) 되더라도 상태는 변하지 않는다.
즉, 논리적으로 가득찼다는 사실만 알고 있다면, bit배열은 유지할 필요가 없다. (=Flag만 유지하면 된다)

HyperSet 에서는 아이템 갯수와 블럭크기를 비교하여, 가득찬 상태라면 bit배열을 null로 초기화하여 메모리를 절약한다.
만약, remove 연산이 필요한 경우라면 물리적인 bit배열을 재생성 하는 단계를 거치기 때문에 다른 연산에 대한 걱정은 하지 않아도 된다.


### 3.3 단일 값의 메모리 낭비 방지 (AloneSet.java) ###

숫자의 분포가 희소성이 커서 블럭내의 bit배열의 낭비되는 경우가 발생할 수 있다.
최악의 경우를 회피하기위해서 하나의 아이템일 경우는 AloneSet이라는 단일 값에 대한 처리가 가능한 구현체를 사용한다.
2건이상의 아이템이 블럭에 들어올때서야, FidxedBitSet 을 이용하는 구조를 갖는다.




## 4. 사용법 ##
Set 인터페이스를 사용했기 때문에 사용법은 기존 Set 자료구조와 동일하다.
단, 현재는 integer 값으로만 제한되어있다.

## 5. 상세내용 ##
### 5.1 숫자 분포가 조밀한 경우에 유용함 ###
BitSet의 단점을 보완하여 많은 아이템을 적은 메모리로 상태를 알 수 있다. 
하지만, 아이템의 분포가 희소성있게 흩어진 경우 HashSet보다 유용하지 못한 경우가 있다. 
(=BitSet을 단점을 보완했으나 기본 아이디어는 유사하므로)

예를 들어, HyperSet은 아래와 같이 분포가 조밀한 경우 효용성이 극대화 된다.


(1) HyperSet이 효율적인 데이터 : 블럭사이즈가 100이라고 가정한 경우, 이럴땐 블럭 2개만 사용

	1 2 3 4 5 7 55 56 58 70 100 111 112 113 115 115 114 120 150 156 157 158 159


(2) HyperSet이 비효율적인 데이터 : 블럭사이즈가 100이라고 가정한 경우, 숫자마다 블럭하나를 사용해서 메모리 낭비가 크다.
(= 하나의 블럭에 하나의 값만 들어가는 문제를 해결하기 위해 AloneSet라는 단일값용  Set 을 사용한다)

	1 101 201 305 406

### 5.2 테스트 방법 ###
아래와 같은 랜덤 숫자를 이용하여 테스트 해 볼수 있다.
Random 값이 1~10억의 범위의 숫자를 처리한다면, 매우 흡족한 성능을 보이지만, 분포가 훨씬큰 값의 범위 (예: 1~20억단위) 라면 오히여  HashSet 보다 성능이 안좋아진다.
이는  블럭구조라는 특이성으로 데이터 분포가 데이터 메모리 사용률에 영향을 주는것을 알 수 있다.
아래 예제는 동일 머신환경에서 돌렸을때의 결과이므로 참고하자.


예제1) 1~20억 사이의 랜덤값을 HashSet을 이용한 경우 (207만개이 아이템 이용 가능하다)

    [code]
    final Set<Integer> set = new HashSet();
    final int limit = 2000000000; // 20억
    Random r = new Random();
    for(int i=1; i<=10000000; i++) {
        int n = Math.abs(r.nextInt(limit)+1);
        set.add(n);
        if (i%10000==0) {
            System.out.println("i="+i +", n=" + n + ", size="+set.size());
        }
    }
    System.out.println(set.size());
    
    
    [result]
    i=10000, n=1864790387, size=10000
    i=20000, n=267272861, size=20000
    ... 생략 ...
    i=2060000, n=1072402759, size=2058905
    i=2070000, n=1292911596, size=2068899
    Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
    	at java.util.HashMap.addEntry(HashMap.java:753)
    	at java.util.HashMap.put(HashMap.java:385)
    	at java.util.HashSet.add(HashSet.java:200)
    	at net.indf.collection.TestMain.main(TestMain.java:26)
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    	at java.lang.reflect.Method.invoke(Method.java:597)
    	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)



예제2) 1~20억 사이의 랜덤값을 HyperSet 으로 이용한 경우 (5만건 정도)

    [code]
    final Set<Integer> set = new HyperSet();
    final int limit = 2000000000; // 20억
    Random r = new Random();
    for(int i=1; i<=10000000; i++) {
        int n = Math.abs(r.nextInt(limit)+1);
        set.add(n);
        if (i%10000==0) {
            System.out.println("i="+i +", n=" + n + ", size="+set.size());
        }
    }
    System.out.println(set.size());
    
    
    [result]
     i=10000, n=356295280, size=10000
     i=20000, n=1260534668, size=20000
     i=30000, n=1487645544, size=30000
     i=40000, n=1679554431, size=40000
     i=50000, n=934191284, size=49999
     Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at net.indf.collection.FixedBitSet.clear(FixedBitSet.java:276)
        at net.indf.collection.FixedBitSet.<init>(FixedBitSet.java:35)
        at net.indf.collection.HyperSet.add(HyperSet.java:80)
        at net.indf.collection.HyperSet.add(HyperSet.java:10)
        at net.indf.collection.TestMain.main(TestMain.java:26)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)

예제3) 1~20억 사이의 랜덤값을 HyperSet 으로 이용한 경우 (단, 블럭의 크기를 2048로 변경한 경우)

    [code]
    final Set<Integer> set = new HyperSet(2048);//==> 숫자 인접도가 떨어진다면 블럭크기를 작게 잡으면 유용하다.
    final int limit = 2000000000; // 20억
    Random r = new Random();
    for(int i=1; i<=10000000; i++) {
        int n = Math.abs(r.nextInt(limit)+1);
        set.add(n);
        if (i%10000==0) {
            System.out.println("i="+i +", n=" + n + ", size="+set.size());
        }
    }
    System.out.println(set.size());
    
    [result]
    i=10000, n=851757464, size=10000
    i=20000, n=1332679172, size=20000
        ...  생략 ...
    i=940000, n=488216681, size=939800
    i=950000, n=1544252811, size=949794
    i=960000, n=1935780660, size=959787
    Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at net.indf.collection.FixedBitSet.clear(FixedBitSet.java:276)
        at net.indf.collection.FixedBitSet.<init>(FixedBitSet.java:35)
        at net.indf.collection.HyperSet.add(HyperSet.java:80)
        at net.indf.collection.HyperSet.add(HyperSet.java:10)
        at net.indf.collection.TestMain.main(TestMain.java:26)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)
    
    
예제4) 1~10억 사이의 랜덤값을 HyperSet 으로 이용한 경우라면  HashSet보다 훨씬 효율적이다.

    [code]
    final Set<Integer> set = new HyperSet(2048);//==> 숫자 인접도가 떨어진다면 블럭크기를 작게 잡으면 유용하다.
    final int limit = 1000000000; // 10억
    Random r = new Random();
    for(int i=1; i<=10000000; i++) {
        int n = Math.abs(r.nextInt(limit)+1);
        set.add(n);
        if (i%10000==0) {
            System.out.println("i="+i +", n=" + n + ", size="+set.size());
        }
    }
    System.out.println(set.size());
    
    [result]
    i=10000, n=707133770, size=10000
    i=20000, n=667099207, size=20000
    ...생략...
    i=9980000, n=802200508, size=9930404
    i=9990000, n=465487717, size=9940303
    i=10000000, n=651885667, size=9950197
    9950197
