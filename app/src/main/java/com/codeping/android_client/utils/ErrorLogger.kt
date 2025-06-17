package com.codeping.android_client.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ErrorLogger {
    private const val TAG = "CodePing_ErrorLogger"
    private const val ERROR_LOG_FILE = "codeping_errors.log"
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    fun logError(
        context: Context,
        error: Throwable,
        location: String,
        userJourney: String,
        additionalInfo: String = ""
    ) {
        val timestamp = dateFormat.format(Date())
        val errorDetails = buildString {
            appendLine("=== ERROR LOG ===")
            appendLine("Timestamp: $timestamp")
            appendLine("Location: $location")
            appendLine("User Journey: $userJourney")
            appendLine("Error Type: ${error.javaClass.simpleName}")
            appendLine("Error Message: ${error.message}")
            appendLine("Localized Message: ${error.localizedMessage}")
            appendLine("Cause: ${error.cause?.message ?: "None"}")
            if (additionalInfo.isNotEmpty()) {
                appendLine("Additional Info: $additionalInfo")
            }
            appendLine("Stack Trace:")
            appendLine(error.stackTraceToString())
            appendLine("==================")
            appendLine()
        }
        
        // Log to console
        Log.e(TAG, errorDetails)
        
        // Write to file
        writeToFile(context, errorDetails)
    }
    
    fun logInfo(context: Context, message: String) {
        Log.i("CodePing_Info", message)
        
        try {
            val logEntry = """
                === INFO LOG ===
                Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}
                Message: $message
                ==================
                
            """.trimIndent()
            
            writeToFile(context, logEntry)
        } catch (e: Exception) {
            Log.e("ErrorLogger", "Failed to write info log", e)
        }
    }
    
    fun logAuthError(
        context: Context,
        error: Throwable,
        authMethod: String,
        userJourney: String
    ) {
        logError(
            context = context,
            error = error,
            location = "Authentication - $authMethod",
            userJourney = userJourney,
            additionalInfo = "Auth Method: $authMethod"
        )
    }
    
    private fun writeToFile(context: Context, errorDetails: String) {
        try {
            val file = File(context.filesDir, ERROR_LOG_FILE)
            FileWriter(file, true).use { writer ->
                writer.append(errorDetails)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write error to file", e)
        }
    }
    
    fun getErrorLogFile(context: Context): File {
        return File(context.filesDir, ERROR_LOG_FILE)
    }
    
    fun clearErrorLog(context: Context) {
        try {
            val file = File(context.filesDir, ERROR_LOG_FILE)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear error log", e)
        }
    }
}
