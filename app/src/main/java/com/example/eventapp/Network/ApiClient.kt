package com.example.eventapp.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // PILIH SALAH SATU SESUAI FOLDERMU:

    // Kalau di browser kamu buka:
    // http://localhost/proyek%20akhir/event-api-php.php
    // maka:
    private const val BASE_URL = "http://10.0.2.2/proyek%20akhir/"
//    private const val BASE_URL = "http://10.30.208.26/proyek%20akhir/"

    // (Kalau nanti kamu rename folder jadi "proyek-akhir" tanpa spasi,
    // tinggal ganti baris di atas jadi:
    // private const val BASE_URL = "http://10.0.2.2/proyek-akhir/"
    // )

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                // HARUS berakhir dengan '/'
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
