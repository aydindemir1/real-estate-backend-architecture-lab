# Lombok

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented  
**Scope:** Java boilerplate reduction through annotation processing

## 1. Nedir?

Project Lombok, Java source code'unda getter, setter, constructor ve builder gibi tekrar eden boilerplate kodları annotation processing yoluyla generate eden library'dir.

## 2. Nasıl çalışır?

Compile sırasında annotation processor devreye girer.

```text
Java Source + Lombok Annotations
        |
        v
Annotation Processing
        |
        v
Compiler AST transformation
        |
        v
Compiled bytecode
```

## 3. Yaygın annotation'lar

- `@Getter`
- `@Setter`
- `@Builder`
- `@NoArgsConstructor`
- `@AllArgsConstructor`
- `@RequiredArgsConstructor`
- `@EqualsAndHashCode`
- `@ToString`
- `@Data`

## 4. Ne işe yarar?

- boilerplate azaltır
- code readability artırabilir
- constructor injection kullanımını kolaylaştırır
- DTO/model kodunu kısaltır

## 5. Riskler

Lombok görünmeyen generated code üretir.

Yanlış kullanım:
- entity'de `@Data`
- relationship içeren object'lerde recursive `toString`
- yanlış equals/hashCode
- mutable domain model'in gereksiz geniş setter exposure'u

gibi sorunlar oluşturabilir.

## 6. JPA ile dikkat edilmesi gerekenler

JPA entity'lerde:
- no-args constructor ihtiyacı
- proxy semantics
- equals/hashCode
- lazy relation
- toString recursion

dikkatle yönetilmelidir.

## 7. Constructor Injection

Spring component'lerinde final field + `@RequiredArgsConstructor` constructor injection boilerplate'ini azaltabilir.

Ancak generated constructor'ın mantığını anlamak gerekir.

## 8. Avantajları

- daha az boilerplate
- daha kısa class
- developer productivity
- constructor/builder kolaylığı

## 9. Dezavantajları

- implicit generated code
- IDE/compiler integration dependency
- annotation overuse
- domain semantics'i gizleme riski

## 10. Bu projede nasıl kullanılıyor?

Lombok, service/entity/DTO gibi Java class'larında boilerplate azaltmak için kullanılmaktadır.

Dependency catalog'da version merkezi yönetilir.

## 11. Engineering kuralı

Lombok convenience tool'dur; architecture veya domain design yerine geçmez.

Her annotation'ın oluşturduğu method'lar bilinmelidir.

## 12. Alternatifleri

- explicit Java code
- Java records
- IDE code generation
- Immutables benzeri codegen araçları

## 13. İleri öğrenme konuları

- annotation processing
- AST transformation
- JPA entity equality
- immutability
