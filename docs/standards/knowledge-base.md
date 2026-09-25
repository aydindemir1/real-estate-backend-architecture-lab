# Knowledge Base Maintenance Standard

Bu standard, proje boyunca öğrenilen Architecture, Approach, Principle, Pattern, Technology ve Protocol bilgilerinin kalıcı, tekrar kullanılabilir ve proje implementation dokümanlarından ayrıştırılmış biçimde tutulmasını tanımlar.

## 1. Amaç

Knowledge Base'in amacı yalnızca "hangi teknolojiyi kullandık?" sorusunu cevaplamak değildir.

Her canonical konu dokümanı mümkün olduğunca şu soruları cevaplamalıdır:

1. Nedir?
2. Hangi problemi çözer?
3. Ne işe yarar?
4. Hangi senaryolarda kullanılır?
5. Hangi senaryolarda tercih edilmemelidir?
6. Temel kavramları nelerdir?
7. İç mimarisi nasıldır?
8. Runtime/işleyiş mekanizması nasıldır?
9. Temel özellikleri nelerdir?
10. Avantajları, dezavantajları ve trade-off'ları nelerdir?
11. Alternatifleri nelerdir?
12. İlgili Architecture/Pattern/Principle ilişkileri nelerdir?
13. Production considerations nelerdir?
14. Bu projede nasıl ve hangi kapsamda kullanılmıştır?
15. İlk hangi Day'de eklenmiş veya anlamlı biçimde uygulanmıştır?
16. İlgili proje dosyaları nelerdir?
17. Hangi ileri konular ayrıca öğrenilmelidir?

## 2. Klasör yapısı

```text
docs/knowledge-base/
├── README.md
├── architectures/
├── approaches/
├── principles/
├── patterns/
├── technologies/
├── protocols/
└── by-day/
```

Project-specific architecture kararları `docs/architecture`, ADR'ler `docs/adr`, engineering kuralları `docs/standards` altında kalır.

Knowledge Base bu dokümanların yerine geçmez.

## 3. Canonical document rule

Bir kavram için yalnızca bir canonical bilgi dosyası bulunmalıdır.

Örnek:

```text
patterns/database-per-service.md
technologies/rabbitmq.md
technologies/cassandra.md
approaches/polyglot-persistence.md
```

Aynı kavram sonraki Day'lerde tekrar kullanıldığında yeni dosya açılmaz. Mevcut canonical doküman gerekiyorsa genişletilir.

## 4. By-Day rule

`by-day/day-XX.md` dosyaları konu açıklamasını tekrar etmez.

Yalnızca ilgili Day'de:
- ilk kez kullanılan,
- anlamlı biçimde uygulanan,
- veya öğrenme kapsamı belirgin biçimde genişletilen

canonical Knowledge Base başlıklarını indeksler.

## 5. Implementation state

"Projede kullanılıyor" ile "altyapısı hazırlandı" ayrıştırılmalıdır.

Önerilen durumlar:
- Planned
- Infrastructure Ready
- Implemented
- Integrated
- Verified
- Superseded

Örneğin Day 7 sonunda MySQL AgentService için `Infrastructure Ready` durumundadır; application-level persistence integration Day 8'de yapılacaktır.

## 6. Day completion rule

Her Day sonunda Knowledge Base impact review zorunludur.

Kontrol:
1. Yeni Architecture var mı?
2. Yeni Approach var mı?
3. Yeni Principle var mı?
4. Yeni Pattern var mı?
5. Yeni Technology var mı?
6. Yeni Protocol/Format var mı?
7. Mevcut bir canonical dokümanın kapsamı genişledi mi?
8. İlgili `by-day/day-XX.md` güncellendi mi?

Yeni konu yoksa bu da bilinçli olarak doğrulanır.

## 7. Definition of Done

Her Day için Definition of Done'a şu madde eklenir:

> Knowledge Base impact reviewed and updated.

## 8. İçerik kalitesi

Dokümanlar yalnız tanım veya teknoloji listesi olmamalıdır.

Önemli konularda:
- internal architecture,
- runtime flow,
- failure modes,
- performance characteristics,
- consistency semantics,
- operational concerns,
- security implications,
- trade-off'lar,
- alternatives,
- project-specific usage

anlatılmalıdır.

Derinlik, konunun projedeki önemine göre ayarlanır.

## 9. Duplication avoidance

Aşağıdaki ayrım korunur:

- `docs/knowledge-base`: kavramın genel mühendislik bilgisi
- `docs/architecture`: bu projenin architecture tasarımı
- `docs/adr`: neden belirli bir karar alındı
- `docs/standards`: implementation sırasında uyulacak kurallar
- `docs/roadmap`: ne zaman ve hangi scope ile uygulanacağı

Bir dokümandaki bilgi diğerine kopyalanmak yerine linklenir.
