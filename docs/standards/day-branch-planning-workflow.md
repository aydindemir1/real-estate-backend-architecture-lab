# Day Branch / Planning Branch Workflow Standard

## Amaç

Bu standard, Day bazlı implementation branch'leri ile `docs/backend-roadmap-design` planning branch'inin rollerini ayırır ve her Day sonunda dokümantasyonun nasıl senkronize edileceğini tanımlar.

## 1. main

`main` baseline branch'tir.

Day implementation çalışmaları sırasında doğrudan değiştirilmez. Day branch'leri `main` üzerinde paralel ve bağımsız şekilde açılmaz; cumulative ilerleme korunur.

## 2. Day branch'leri cumulative snapshot'tır

Her yeni Day branch'i bir önceki Day branch'inden türetilir.

Örnek:

```text
main
  └── day/07-build-data-infra
        └── day/08-agent-mysql-clean
              └── day/09-...
                    └── day/10-...
```

Buna göre:

- Day 7 branch'i yalnız Day 1–7 kod ve dokümantasyon state'ini içerir.
- Day 8 branch'i Day 1–8 kod ve dokümantasyon state'ini içerir.
- Day 9 branch'i Day 1–9 kod ve dokümantasyon state'ini içerir.

Bir Day branch'ine gelecekteki Day implementation veya gerçekleşmiş gibi yazılmış future documentation eklenmez.

Kapanmış eski Day branch'leri sonraki günlerin değişiklikleriyle geriye dönük güncellenmez. Yalnız açıkça onaylanan kritik historical correction istisnadır.

## 3. docs/backend-roadmap-design

`docs/backend-roadmap-design` canonical plan/program ve cumulative documentation branch'idir.

Bu branch:

- roadmap ve future plan'ları tutabilir,
- tamamlanan her Day sonunda o güne kadar gerçekleşmiş implementation state'ini dokümantasyon olarak yansıtır,
- Knowledge Base, architecture, standards ve service documentation güncellemelerini taşır.

Day N kapanışında yalnız:

- Day N'e ait plan/decision/completion dokümanları,
- Day N implementation'ının gerçekten etkilediği shared/canonical dokümanlar

güncellenir.

Day N kapanışı bahanesiyle Day N+1 ve sonraki günlerin bağımsız planları yeniden yazılmaz.

## 4. Kodun planning branch'e taşınması

Şimdilik `docs/backend-roadmap-design` için code synchronization zorunlu değildir.

Canonical implementation code, en güncel Day branch'inde bulunur.

Planning branch'e kod taşıma politikası ayrıca kararlaştırılacaktır. Bu karar verilene kadar planning branch dokümantasyon ağırlıklı kalır.

## 5. Day Close Protocol

Day N kapanmadan önce:

1. Day N implementation scope tamamlanır.
2. İlgili automated tests eklenir.
3. Mümkün olan build/test verification çalıştırılır.
4. Day N branch içindeki service, architecture, roadmap ve Knowledge Base dokümanları actual implementation'a göre güncellenir.
5. Day N'e ait plan ve locked decision dokümanları Day N branch'inde bulunur.
6. Knowledge Base impact review yapılır.
7. Aynı gerekli documentation değişiklikleri `docs/backend-roadmap-design` branch'ine senkronize edilir.
8. Unrelated future Day dokümanlarına dokunulmaz.
9. GitHub tool/write sonucu doğrulanmadan commit veya push tamamlandı kabul edilmez.
10. Day N+1 branch'i Day N branch'inden oluşturulur.

## 6. Source of truth

Completed Day implementation gerçeği için o Day branch'indeki code esas alınır.

Canonical cumulative documentation için `docs/backend-roadmap-design` esas alınır.

İkisi arasında completed-Day factual fark oluşursa actual implementation incelenir ve documentation iki branch'te tekrar hizalanır.

## 7. Örnek

Day 8 sonunda:

`day/08-agent-mysql-clean`:
- Day 1–8 implementation code
- Day 1–8 için gerekli current documentation
- Day 8 exact plan
- Day 8 locked decisions
- Day 8 completion/actual implementation documentation
- Day 8 Knowledge Base additions

`docs/backend-roadmap-design`:
- mevcut future roadmap korunur
- Day 8 actual documentation state eklenir/güncellenir
- Day 8'in etkilediği canonical Knowledge Base/architecture/standards dokümanları güncellenir
- unrelated Day 9+ planlarına dokunulmaz

`day/07-build-data-infra`:
- değişmeden Day 1–7 historical snapshot olarak kalır.
