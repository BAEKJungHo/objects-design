# JavaBeans, POJO, Entity, VO, DTO

## POJO(Plain Old Java Object)

- __정의__
    - 특정 프레임워크에 대한 참조가 없는 간단한 유형

```java
public class EmployeePojo {

    public String firstName;
    public String lastName;
    private LocalDate startDate;

    public EmployeePojo(String firstName, String lastName, LocalDate startDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
    }

    public String name() {
        return this.firstName + " " + this.lastName;
    }

    public LocalDate getStart() {
        return this.startDate;
    }
}
```

이 클래스는 프레임워크에 연결되지 않으므로 모든 Java 프로그램에서 사용할 수 있다.

@Service 나 @ComponentScan 같은 어노테이션을 사용중인 클래스는 POJO 일까? 아닐까? 

POJO 의 엄밀한 정의로만 보면 아닌게 맞다.

하지만, [Wikipedia](https://en.wikipedia.org/wiki/Plain_old_Java_object) 내용을 참고하면 다음과 같다.

> 객체(실제로는 클래스)가 주석이 추가되기 전에 POJO 였고 주석이 제거되면 POJO 상태로 돌아갈 경우 여전히 POJO 로 간주될 수 있다는 아이디어입니다.
>
> The idea is that if the object (actually class) were a POJO before any annotations were added, and would return to POJO status if the annotations are removed then it can still be considered a POJO.

즉, 특정 프레임워크에 종속적인 어노테이션을 제거했을 때 POJO 가 된다면, POJO 로 간주 된다는 것이다.

### POJO with Reflection

POJO 에 있는 속성들을 `commons-beanutils` 를 사용하여 조회해보자.

```java
@Test
void pojoWithReflection() {
    List<String> propertyNames = Arrays.stream(PropertyUtils.getPropertyDescriptors(EmployeePojo.class))
                    .map(PropertyDescriptor::getDisplayName)
                    .collect(Collectors.toList());
    assertThat(propertyNames).containsExactly("start"); // test passed
}
```

POJO 클래스에서 getter 메서드 가 `getStart()` 하나 뿐이다. 리플렉션을 통해서 속성을 조회할때 get 뒤에 붙은 이름으로 조회한다.

## JavaBeans

- __정의__
    - 자바로 작성된 소프트웨어 컴포넌트 (by wikipedia)
    - 표준 컨벤션
    - JavaBean 도 POJO 이다. 대신 구현 방법에 대한 제약사항들이 추가되었다고 보면 된다. 
- __지켜야할 관례__
    - `액세스 수준` 
        – 속성은 비공개이며 getter 및 setter를 노출한다.
    - `메서드 이름` 
        – getter 및 setter는 getX 및 setX 규칙을 따른다.(부울의 경우 isX 는 getter에 사용할 수 있음)
    - `기본 생성자` 
        – 예를 들어 역직렬화 중에 인수를 제공하지 않고 인스턴스를 생성할 수 있도록 인수가 없는 생성자가 있어야 한다.
    - `직렬화 가능` 
        – 직렬화 가능 인터페이스를 구현하면 상태를 저장할 수 있다.

> 빈즈(Beans) : 특정한 일을 독립적으로 수행하는 컴포넌트
>
> 컴포넌트(Component) : 다른 무언가를 만들기 위한 부품, 컴포넌트는 각각 독립적인 기능이 있으며, 컴포넌트를 조합해 다양한 형태의 결과물을 만들 수 있다.

### The following is the definition of a legal JavaBean

```java
public class EmployeeBean implements Serializable {

    private static final long serialVersionUID = -3760445487636086034L;
    private String firstName;
    private String lastName;
    private LocalDate startDate;

    public EmployeeBean() {
    }

    public EmployeeBean(String firstName, String lastName, LocalDate startDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    //  additional getters/setters
}
```

### JavaBeans with Reflection

```java
@Test
void pojoWithReflection() {
    List<String> propertyNames = Arrays.stream(PropertyUtils.getPropertyDescriptors(EmployeeBean.class))
            .map(PropertyDescriptor::getDisplayName)
            .collect(Collectors.toList());
    assertThat(propertyNames).containsExactly("firstName", "lastName", "startDate"); // test passed
}
```

모든 속성이 조회되는 것을 알 수 있다.

따라서, `JavaBeans` 생성 규약을 잘 지키면, 리플렉션을 사용하여 어떤 속성들이 있는지 알 수도 있으며 많은 Java 라이브러리가 기본적으로 JavaBean 명명 규칙이라는 것을 지원하기 때문에 라이브러리를 사용함에 있어서 이점이 있어보인다.

### 단점

- __변경성__
    - JavaBeans 는 setter 메소드로 인해 변경 가능하다. 이는 동시성 또는 일관성 문제로 이어질 수 있다.
- __상용구__ 
    - 우리는 모든 속성에 대해 getter 를 도입해야 하고 대부분의 경우 setter 를 도입해야 한다. 이 중 대부분은 불필요할 수 있다.
- __Zero-argument Constructor__
    - 객체가 유효한 상태에서 인스턴스화되도록 하기 위해 종종 생성자에 인수가 필요하지만 JavaBean 표준은 Zero-argument 생성자를 제공하도록 요구합니다.

### 정리

- POJO 는 특정 프레임워크에 종속적이지 않은 Java 객체이다.
- JavaBean 은 엄격한 규칙 집합을 사용하는 특수한 유형의 POJO 이다. 

## References

- https://velog.io/@dion/what-is-javabeans-and-why-use-javabeans
- https://bohemichan.tistory.com/21
- https://www.baeldung.com/java-pojo-class
