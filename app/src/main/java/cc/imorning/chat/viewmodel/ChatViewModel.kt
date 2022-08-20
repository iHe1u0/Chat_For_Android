package cc.imorning.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.imorning.common.database.dao.AppDatabaseDao

class ChatViewModel(private val appDatabaseDao: AppDatabaseDao) : ViewModel()

// class ChatViewModelFactory(
//     private val appDatabaseDao: AppDatabaseDao
// ) : ViewModelProvider.Factory {
//
//     override fun <T : ViewModel> create(modelClass: Class<T>): T {
//         if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
//             @Suppress("UNCHECKED_CAST")
//             return ChatViewModel(appDatabaseDao) as T
//         }
//         throw IllegalArgumentException("Unknown ViewModel class")
//     }
//
// }