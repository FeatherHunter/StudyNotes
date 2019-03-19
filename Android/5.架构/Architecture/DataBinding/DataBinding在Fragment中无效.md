
转载请注明链接: https://blog.csdn.net/feather_wch/article/details/88400574

# DataBinding在Fragment中无效

版本号:2019-03-17(17:50)

---

@[toc]
## Fragment中无效

在Fragment中使用Databinding的时候，遇到无论如何设置都无法生效。后来发现Fragment有特殊的使用方法。

### 错误示范

1、错误的使用方法如下:
```java
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
{
    View view = inflater.inflate(R.layout.modify_username_layout, null);

    // 1、对布局需要绑定的内容进行加载
    binding = DataBindingUtil.inflate(inflater, R.layout.modify_username_layout, container, false);

    // 2、默认的提示语：请输入username
    mUsernameErrorMsg = new ErrorMsg(_mActivity.getResources().getString(R.string.modify_username_hint_empty), true);
    // 3、绑定数据
    binding.setError(mUsernameErrorMsg);

    return view;
}

```


### 正确使用

2、正确的使用方法
```java
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
{
    // 1、对布局需要绑定的内容进行加载
    mBinding = DataBindingUtil.inflate(inflater, R.layout.modify_username_layout, container, false);
    // 2、获取到视图
    View view = mBinding.getRoot();
    // 3、绑定数据
    mUsernameErrorMsg = new ErrorMsg(_mActivity.getResources().getString(R.string.modify_username_hint_empty), true); // 默认的提示语：请输入username
    mBinding.setError(mUsernameErrorMsg);

    return view;
}
```
