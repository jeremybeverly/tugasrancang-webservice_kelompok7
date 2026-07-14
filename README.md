# 🛠️ Panduan Langkah-Langkah Git (Wajib Diikuti!)

Bagi teman-teman yang belum terbiasa dengan Git, ikuti urutan perintah ini setiap kali kalian ingin mengerjakan tugas masing-masing.

---

### Langkah 1: Ambil Project Terbaru & Pindah ke Main
Sebelum mulai mengetik kode apa pun, pastikan posisi kalian berada di branch `main` utama dan kodenya adalah yang paling update dari GitHub.
```bash
git checkout main
git pull origin main
```

### JANGAN LANGSUNG CODING DI MAIN! Buat branch kalian sendiri terlebih dahulu lewat command-command ini:

* Anggota 2: git checkout -b feature/route-one
* Anggota 3: git checkout -b feature/route-two
* Anggota 4: git checkout -b feature/jwt-filter
* Anggota 5: git checkout -b feature/bff-dashboard
* Anggota 6: git checkout -b feature/rate-limiting

(Setelah mengetik perintah di atas, barulah kalian silakan buka vscode atau tools lain baru mulai coding).

### Langkah 3: Simpan dan Upload Tugas ke GitHub
Jika tugas sudah selesai dibuat dan aplikasi sudah dites, jalankan 3 baris perintah ini secara berurutan di terminal untuk meng-upload kode kalian:
```bash
git add .
git commit -m "feat: menyelesaikan tugas anggota X"
git push origin HEAD
```
