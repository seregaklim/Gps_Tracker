package com.seregaklim.mytracker.utils

import android.app.AlertDialog
import android.content.Context
import com.seregaklim.mytracker.R

object DialogManager {
   fun showLocEnabledDialog (context: Context,lister:Listener){

      val builder=AlertDialog.Builder(context)
      val dialog = builder.create()
      dialog.setTitle(R.string.location_disabled)
      dialog.setMessage(context.getString(R.string.location_dialog_message))
     dialog.setButton(AlertDialog.BUTTON_POSITIVE,"${R.string.yes}")
     {
       _,_ ->lister.onCklick()
      }

     dialog.setButton(AlertDialog.BUTTON_POSITIVE,"${R.string.no}")

      {
               //закрываем
            _,_ ->dialog.dismiss()
      }

   }

   interface Listener{
      fun onCklick()

   }

}