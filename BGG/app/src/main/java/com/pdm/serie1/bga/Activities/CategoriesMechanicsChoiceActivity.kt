package com.pdm.serie1.bga.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.pdm.serie1.bga.R
import kotlinx.android.synthetic.main.activity_categories_mechanics_choice.*
import org.intellij.lang.annotations.JdkConstants

class CategoriesMechanicsChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_mechanics_choice)
        val parameter = intent.getStringExtra("parameter")
        val itemsArray = intent.getStringArrayExtra("items")
        title = "$parameter Choice"

        itemsArray!!.forEach {
            getSwitchWithname(it)
        }

    }

    private fun getSwitchWithname(name: String?) {
        val textView = TextView(this)
        textView.text = name
        val switch = Switch(this)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        linearLayout.addView(switch)
        linearLayout.addView(textView)

        switches_layout.addView(linearLayout)
    }
}
