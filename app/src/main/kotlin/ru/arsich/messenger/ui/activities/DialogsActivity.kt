package ru.arsich.messenger.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.arsich.messenger.R
import ru.arsich.messenger.ui.fragments.DialogsFragment


class DialogsActivity : AppCompatActivity() {
    companion object {
        fun startFrom(context: Context) {
            val intent = Intent(context, DialogsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.dialogs)
        setContentView(R.layout.activity_dialogs)

        if (savedInstanceState == null) {
            val fragment = DialogsFragment.create()
            supportFragmentManager.beginTransaction()
                    .add(R.id.contentFrame, fragment, fragment.tag)
                    .commit()
        }
    }
}
