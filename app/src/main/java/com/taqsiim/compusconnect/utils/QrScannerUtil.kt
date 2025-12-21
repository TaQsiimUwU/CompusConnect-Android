package com.taqsiim.compusconnect.utils

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.tasks.await

class QrScannerUtil(private val context: Context) {

    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)

    suspend fun scanQrCode(): String? {
        return try {
            // Ensure the module is installed
            val moduleInstallRequest = ModuleInstallRequest.newBuilder()
                .addApi(scanner)
                .build()

            ModuleInstall.getClient(context)
                .installModules(moduleInstallRequest)
                .await()

            val barcode = scanner.startScan().await()
            barcode.rawValue
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
