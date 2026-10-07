# Day 46 — Linux, process ve network temeli

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Container ve JVM arızalarını host seviyesinde teşhis edebilmek.

Önkoşullar: [Day 45](day-45-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/46-linux-runtime-foundation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Linux process/signal; filesystem izinleri; TCP/DNS; cgroups/namespaces; JVM process modeli

## Uygulama görevleri

1. Donanım, OS/WSL2/Linux, CPU/RAM/disk bütçesi ve kullanılacak terminal araçlarını envanterle; tüm ağır servislerin aynı anda çalışmasını şart koşma.
2. Java process’in PID, port, open file ve environment ilişkisini ps/ss/lsof veya mevcut eşdeğer araçlarla gözle; secret değerlerini kanıta koyma.
3. SIGTERM/SIGKILL, exit code, parent/child process ve stdout/stderr davranışını küçük mevcut servis üzerinde incele.
4. İzinler, UID/GID, bind mount ownership ve dosya yazma hatasını izole geçici dizinde göster.
5. DNS resolution, loopback, host/container adresi ve TCP bağlantı hatalarını teşhis et; localhost anlamını çalışma ortamına göre ayır.
6. cgroups/namespaces’in CPU/memory/process isolation rolünü belgeleyip Day 48–49 testlerine önkoşul oluştur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `docs/devops/lab-environment.md`
- `docs/runbooks/linux-runtime-troubleshooting.md`
- `scripts/devops/diagnose-runtime.sh`

- `docs/evidence/day-46/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(lab): yerel ortam ve kaynak bütçesini tanımla`
2. `feat(lab): secretsız runtime teşhis komutlarını ekle`
3. `test(lab): process port ve izin hata tatbikatlarını doğrula`
4. `docs(linux): signal network ve isolation bulgularını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Port çakışmasını sebep/çözüm/tekrar doğrulama ile kaydet.
2. SIGTERM ile düzenli kapanış ve SIGKILL ile zorla sonlandırma farkını göster.
3. İzin hatasında root’a geçerek kalıcı çözüm iddiası yapma.

## Kapanış ölçütleri

- [ ] Öğrenme konuları proje üzerinden açıklanabiliyor; karşılaştırma kararları gerekçeli.
- [ ] Görevler tamamlandı; hedef dosyalar actual implementation'a uyarlanıp commit edildi.
- [ ] Yukarıdaki doğrulamalar gerçek yerel ortamda çalıştırıldı; başarısız sonuçlar çözüm veya açık sınırlama olarak kaydedildi.
- [ ] Secret, token, private key ve kişisel veri evidence veya Git geçmişine girmedi.
- [ ] Kurulum, tekrar çalıştırma ve güvenli temizlik/runbook adımları doğrulandı; veri silme kapsamı açık.
- [ ] Gerekli CI kontrolleri geçti; sürüm/digest ve kaynak tüketimi kaydedildi.
- [ ] `Planlandı / Infrastructure Ready / Implemented / Integrated / Verified / Design Only` durumları yetenek bazında güncellendi; doğrulanmamış kapsam Verified olmadı.
- [ ] ADR, servis belgeleri, evidence ve Knowledge Base etki incelemesi tamamlandı.

## Kapsam sınırı

Host tuning yalnız gözlemlenen ihtiyaca göre yapılır; gerçek kullanıcı verisi veya production host yoktur.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
