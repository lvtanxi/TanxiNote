package com.lv.note.util.Permission

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import java.util.*

/**
 * 权限管理
 * @author
 * *
 * @class PermissionManager
 * *
 * @date 2016-3-25 下午3:54:14
 */
class PermissionManager(private val mObject: Any) {
    private var mPermissions: Array<String>? = null
    private var mRequestCode: Int = 0
    private var mListener: PermissionListener? = null
    // 用户是否确认了解释框的
    private var mIsPositive = false

    fun permissions(vararg permissions: String): PermissionManager {
        this.mPermissions = permissions as Array<String>
        return this
    }

    fun addRequestCode(requestCode: Int): PermissionManager {
        this.mRequestCode = requestCode
        return this
    }

    fun setPermissionsListener(listener: PermissionListener): PermissionManager {
        this.mListener = listener
        return this
    }

    /**请求权限
     * @return PermissionManager
     */
    fun request(): PermissionManager {
        request(mObject, mPermissions!!, mRequestCode)
        return this
    }

    private fun request(obj: Any, permissions: Array<String>, requestCode: Int) {
        // 根据权限集合去查找是否已经授权过
        val map = findDeniedPermissions(getActivity(obj)!!, *permissions)
        val deniedPermissions = map["deny"]
        val rationales = map["rationale"]
        if (deniedPermissions!!.size > 0) {
            // 第一次点击deny才调用，mIsPositive是为了防止点确认解释框后调request()递归调onShowRationale
            if (rationales!!.size > 0 && mIsPositive == false) {
                if (mListener != null) {
                    mListener!!.onShowRationale(this,rationales.toTypedArray())
                }
                return
            }
            if (obj is Activity) {
                ActivityCompat.requestPermissions(obj, deniedPermissions.toTypedArray(), requestCode)
            } else if (obj is Fragment) {
                obj.requestPermissions(deniedPermissions.toTypedArray(), requestCode)
            } else {
                throw IllegalArgumentException(obj.javaClass.name + " is not supported")
            }
        } else {
            if (mListener != null) {
                mListener!!.onGranted()
            }
        }
    }

    /**根据requestCode处理响应的权限
     * @param permissions
     * *
     * @param results
     */
    fun onPermissionResult(permissions: Array<String>, results: IntArray) {
        val deniedPermissions = ArrayList<String>()
        for (i in results.indices) {
            if (results[i] != PackageManager.PERMISSION_GRANTED) {//未授权
                deniedPermissions.add(permissions[i])
            }
        }
        if (deniedPermissions.size > 0) {
            if (mListener != null) {
                mListener!!.onDenied()
            }
        } else {
            if (mListener != null) {
                mListener!!.onGranted()
            }
        }
    }

    private fun findDeniedPermissions(activity: Activity, vararg permissions: String): Map<String, List<String>> {
        val map = ArrayMap<String, List<String>>()
        val denyList = ArrayList<String>()//未授权的权限
        val rationaleList = ArrayList<String>()//需要显示提示框的权限
        for (value in permissions) {
            if (ContextCompat.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                denyList.add(value)
                if (shouldShowRequestPermissionRationale(value)) {
                    rationaleList.add(value)
                }
            }
        }
        map.put("deny", denyList)
        map.put("rationale", rationaleList)
        return map
    }

    private fun getActivity(obj: Any): Activity? {
        if (obj is Fragment) {
            return obj.activity
        } else if (obj is Activity) {
            return obj
        }
        return null
    }

    /**
     * 当用户拒绝某权限时并点击就不再提醒的按钮时，下次应用再请求该权限时，需要给出合适的响应（比如给个展示对话框）
     * @param permission
     */
    private fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        if (mObject is Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale(mObject, permission)
        } else if (mObject is Fragment) {
            return mObject.shouldShowRequestPermissionRationale(permission)
        } else {
            throw IllegalArgumentException(mObject.javaClass.name + " is not supported")
        }
    }

    fun setIsPositive(isPositive: Boolean) {
        this.mIsPositive = isPositive
    }

    companion object {

        fun with(activity: Activity): PermissionManager {
            return PermissionManager(activity)
        }

        fun with(fragment: Fragment): PermissionManager {
            return PermissionManager(fragment)
        }
    }
}
