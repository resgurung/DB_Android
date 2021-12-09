package co.deshbidesh.db_android.db_imageslider.interfaces

import co.deshbidesh.db_android.db_imageslider.constants.ActionTypes



/**
 * Created by Deniz Coşkun on 10/10/2020.
 * denzcoskun@hotmail.com
 * İstanbul
 */
interface TouchListener {
    /**
     * Click listener touched item function.
     *
     * @param  touched  slider boolean
     */
    fun onTouched(touched: ActionTypes)
}