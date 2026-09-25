# MapStruct

**Category:** Technology  
**Introduced:** Day 3  
**Project status:** Implemented  
**Scope:** Compile-time object mapping

## 1. Nedir?

MapStruct, Java object'leri arasında mapping code'unu compile time'da generate eden annotation processor tabanlı mapper framework'üdür.

Tipik kullanım:

```text
Request DTO
   |
   v
Domain / Entity
   |
   v
Response DTO
```

## 2. Hangi problemi çözer?

Manual field mapping:
- repetitive
- error-prone
- verbose

olabilir.

Reflection-based mapper'lar ise runtime overhead ve implicit behavior oluşturabilir.

MapStruct mapping implementation'ını compile time'da generate eder.

## 3. Nasıl çalışır?

```text
@Mapper interface
      |
      v
Annotation Processor
      |
      v
Generated Java Mapper Implementation
      |
      v
Normal compiled bytecode
```

Runtime reflection zorunlu değildir.

## 4. Temel kavramlar

- `@Mapper`
- `@Mapping`
- source/target
- componentModel
- nested mapping
- custom mapping method
- unmapped target policy

## 5. Spring entegrasyonu

`componentModel = "spring"` ile generated mapper Spring bean olarak kullanılabilir.

## 6. Avantajları

- compile-time generation
- type safety
- yüksek performans
- explicit mapping
- refactoring sırasında compile error avantajı

## 7. Dezavantajları

- annotation processor setup
- complex mapping'de configuration artışı
- generated source debugging ihtiyacı
- domain conversion logic ile mechanical mapping'in karışma riski

## 8. Mapping ile business logic farkı

Mapper:
- structural conversion

yapmalıdır.

Mapper içine:
- business rule
- repository access
- workflow logic

konmamalıdır.

## 9. Bu projede nasıl kullanılıyor?

Day 3'te AuthService ve UserProfile flow'larında DTO/model conversion için MapStruct kullanılmıştır.

Day 7'de root build'den zorunlu MapStruct dependency kaldırılıp yalnız ihtiyaç duyan module'lere bırakılmıştır.

## 10. Compile warning'leri

Unmapped property warning'leri:
- gerçek mapping eksikliği
- bilinçli ignore
- model drift

açısından incelenmelidir.

Warning'i yalnız susturmak yerine sebebi anlaşılmalıdır.

## 11. Alternatifleri

- manual mapping
- ModelMapper
- reflection-based mapping
- constructor/factory mapping

## 12. Production considerations

- generated source CI'da doğrulanmalı
- unmapped field policy tanımlanmalı
- domain invariant mapper'a taşınmamalı
- mapping boundary explicit tutulmalı

## 13. İleri öğrenme konuları

- nested mappings
- update mappings
- qualifiers
- custom converters
- immutable targets
