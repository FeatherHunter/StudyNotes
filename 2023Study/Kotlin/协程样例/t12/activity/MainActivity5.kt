package com.derry.kt_coroutines.t12.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.derry.kt_coroutines.R
import com.derry.kt_coroutines.databinding.ActivityMain5Binding
import com.derry.kt_coroutines.t12.viewmodel.APIViewModel
import kotlinx.android.synthetic.main.activity_main5.*

/**
 * TODO 3.协程+Retrofit+ViewModel+LiveData+DataBinding网络通信
 *  3.1：Retrofit+ViewModel+LiveData+DataBinding快速搭建
 *  3.2：协程配合上面组件，完成网络加载，展示多组件中协程优点
 */
class MainActivity5 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定DataBinding 与 ViewModel 的关系
        val binding = DataBindingUtil.setContentView<ActivityMain5Binding>(this, R.layout.activity_main5)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(APIViewModel::class.java)
        binding.vm = viewModel

        bt.setOnClickListener { viewModel.requestLogin("Derry-vip", "123456") }
    }
}