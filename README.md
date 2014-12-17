BitmapSet
=========

중복된 아이템을 제외한 유일한 카운트 UC(UniqueCount)를 구하기 위해서는, Java에서 제공하는 Set을 이용하여 중복된 아이템을 제외한 숫자를 카운팅 할 수 있다. 

예를 들어, Hashset을 이용할 경우 256mb의 환경에서는 integer인 경우라도 560만개 수준의 아이템을 넣을수 있다. Bitset을 이용하면 더 많은 아이템을 넣을수 있지만 최대값에 영향을 받기 때문에  '2000000000' 한건만 넣어도 heap space오류가 나게 된다.

최대값에 영향을 안받고, 효율적으로 메모리를 사용하는 UC를 구하기위한 자료구조가 필요하다.

 

## 1. 기존환경 ##
(1) HashSet (256mb환경에서는 약 560만개의 아이템)
    
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



(2) Bitset (item의 최대값에 영향을 받음)

    public static void main(String ...argv){
        BitSet set = new BitSet();
        set.set(80000000);
        set.set(90000000);
        set.set(1300000000);
        System.out.println("size? " + set.cardinality());
    }

	//==> Exception in thread "main" java.lang.OutOfMemoryError: Java heap space



## 2. 기본개념 ##
### 2.1 블럭단위의 메모리 관리 ###
Bitmap 자료구조에서 문제가 되는것은 최대값의 크기만큼의 하나의 큰 배열을 생성하기 때문이다.
이런 낭비를 줄이기 위해 논리적으로 N개의 블럭으로 쪼개어 관리한다.

예를 들어, '40'을 넣었을때 자료구조 차이는 아래와 같다.
이때 논리적 블럭이 모두 0인 블럭은 메모리 생성을 하지 않고 null 상태로 유지한다.
실제구현은 Map<블럭위치, FixedBitMapSet> 의 형태로 관리되어 미사용되는 블럭은 생성하지 않는다.

* BitSet
	*  [00000000 00000000 00000000 00000000  00000001 00000000 00000000 00000000]  
	 
* BitMapSet
	*  [null] [00000001 00000000 00000000 00000000]


### 2.2 Full된 블럭의 메모리 해제 ###

Set에서는 이미 데이터가 존재할 때, 중복되는 아이템을 넣을경우는 기존과 동일하다.

이 성질을 이용하여 이미 블럭이 꽉 차있다면, 해당 범위의 값이 추가되더라도 아무런 행동을 할 필요가 없다. 즉, 논리적으로 꽉찬 bitmap블럭은 유지할 필요가 없다. 이 상태일때는 메모리 절약을 위해 가득찼다는 상태만 체크하고, bitmap용 데이터는 버려버리고 메모리를 절약할 수 있다.

즉, 논리적으로 잘라낸 블럭이 가득찬 경우 그 메모리는 다시 해제가 가능하게 되어 메모리를 회수할 수 있다.  
  



## 3. 사용법 ##
Set인터페이스를 사용했기 때문에 사용법은 기존 Set 자료구조와 동일하다.
