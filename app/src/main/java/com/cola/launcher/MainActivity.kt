package com.cola.launcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("cola_settings", MODE_PRIVATE) }
    private var dark = true
    private var selectedVersion = ""
    private var backgroundUri: Uri? = null
    private var renderScale = "75%"
    private var javaVersion = "21"
    private var jvmArgs = "-Xmx2048M"

    private lateinit var root: LinearLayout
    private lateinit var status: TextView
    private lateinit var playButton: Button
    private lateinit var versionSpinner: Spinner

    private val red = Color.rgb(230, 25, 50)
    private val bgColor get() = if (dark) Color.rgb(17, 20, 29) else Color.rgb(239, 242, 248)
    private val surfaceColor get() = if (dark) Color.rgb(29, 34, 46) else Color.WHITE
    private val fgColor get() = if (dark) Color.WHITE else Color.rgb(27, 31, 42)
    private val mutedColor get() = if (dark) Color.LTGRAY else Color.DKGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        dark = prefs.getBoolean("dark", true)
        selectedVersion = prefs.getString("version", "") ?: ""
        renderScale = prefs.getString("scale", "75%") ?: "75%"
        javaVersion = prefs.getString("java", "21") ?: "21"
        jvmArgs = prefs.getString("jvm", "-Xmx2048M") ?: "-Xmx2048M"
        prefs.getString("background", null)?.let { backgroundUri = Uri.parse(it) }
        render()
    }

    private fun label(value: String, size: Float, color: Int = fgColor, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER_VERTICAL
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun button(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 14f
        setOnClickListener { action() }
    }

    private fun render() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(bgColor)
        }

        val sidebar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(6, 6, 12, 6)
        }
        sidebar.addView(label("🥤 ColaLauncher", 22f, red, true))
        sidebar.addView(label("Minecraft Java Edition", 12f, mutedColor))
        sidebar.addView(Space(this), LinearLayout.LayoutParams(1, 10))
        sidebar.addView(button("⌂  Главная") { render() })
        sidebar.addView(button("▦  Версии") { showInfo("Версии", "Выбери версию в главном меню. Каталог и скачивание добавим следующим этапом.") })
        sidebar.addView(button("◇  Моды") { showInfo("Моды", "Fabric, Forge, NeoForge, OptiFine, Fabric API и CurseForge подключим после загрузчика Minecraft.") })
        sidebar.addView(button("♙  Аккаунт") { showInfo("Аккаунт", "Microsoft-вход и офлайн-профиль реализуем отдельным этапом.") })
        sidebar.addView(button("⚙  Настройки") { showSettings() })
        sidebar.addView(Space(this), LinearLayout.LayoutParams(1, 0, 1f))
        sidebar.addView(label("COLA LAUNCHER • 0.1.0", 10f, mutedColor))
        root.addView(sidebar, LinearLayout.LayoutParams(0, -1, 0.30f))

        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 0, 8, 0)
        }
        root.addView(main, LinearLayout.LayoutParams(0, -1, 0.70f))

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(label("Твой Minecraft. Твои правила.", 18f, fgColor, true),
            LinearLayout.LayoutParams(0, 46, 1f))
        top.addView(button(if (dark) "☾ Тёмная" else "☀ Светлая") {
            dark = !dark
            prefs.edit().putBoolean("dark", dark).apply()
            render()
        })
        top.addView(button("⚙") { showSettings() })
        main.addView(top)

        val logo = label("🥤  COLA LAUNCHER", 29f, red, true).apply {
            gravity = Gravity.CENTER
        }
        main.addView(logo, LinearLayout.LayoutParams(-1, 0, 1f))
        main.addView(label("Выбери версию Minecraft", 14f, mutedColor),
            LinearLayout.LayoutParams(-1, 28))

        val choices = listOf(
            "Выбери версию Minecraft", "1.21.4", "1.21.1",
            "1.20.1", "1.16.5", "1.12.2", "1.8.9"
        )
        versionSpinner = Spinner(this)
        versionSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, choices)
        versionSpinner.setSelection(choices.indexOf(selectedVersion).takeIf { it >= 0 } ?: 0)
        versionSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedVersion = if (position == 0) "" else choices[position]
                prefs.edit().putString("version", selectedVersion).apply()
                updatePlayButton()
            }
        }
        main.addView(versionSpinner, LinearLayout.LayoutParams(-1, 46))

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        playButton = button("▶  ИГРАТЬ") {
            if (selectedVersion.isBlank()) {
                status.text = "Сначала выбери версию Minecraft."
            } else {
                showInfo("Версия $selectedVersion выбрана",
                    "Интерфейс готов. Загрузчик файлов, Java Runtime и реальный запуск Minecraft ещё предстоит подключить.")
            }
        }.apply {
            textSize = 20f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(red)
                cornerRadius = 18f
            }
        }
        bottom.addView(playButton, LinearLayout.LayoutParams(0, 54, 1f))
        bottom.addView(button("🖼 Фон") { pickBackground() },
            LinearLayout.LayoutParams(105, 54).apply { leftMargin = 8 })
        bottom.addView(button("Настройки") { showSettings() },
            LinearLayout.LayoutParams(112, 54).apply { leftMargin = 8 })
        main.addView(bottom, LinearLayout.LayoutParams(-1, 60).apply { topMargin = 6 })

        status = label("Готов к настройке", 12f, mutedColor)
        main.addView(status, LinearLayout.LayoutParams(-1, 25))

        setContentView(root)
        applyBackground()
        updatePlayButton()
    }

    private fun updatePlayButton() {
        if (::playButton.isInitialized) {
            playButton.text = if (selectedVersion.isBlank()) "▶  ИГРАТЬ"
            else "▶  ИГРАТЬ • $selectedVersion"
        }
    }

    private fun showInfo(title: String, message: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(message)
            .setPositiveButton("ОК", null).show()
    }

    private fun showSettings() {
        val options = arrayOf(
            "Переключить тему",
            "Выбрать фон из галереи / файлов",
            "Разрешение рендера: $renderScale",
            "Версия Java: $javaVersion",
            "Аргументы JVM",
            "Рендереры"
        )
        AlertDialog.Builder(this).setTitle("Настройки ColaLauncher")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        dark = !dark
                        prefs.edit().putBoolean("dark", dark).apply()
                        render()
                    }
                    1 -> pickBackground()
                    2 -> AlertDialog.Builder(this).setTitle("Разрешение рендера")
                        .setSingleChoiceItems(arrayOf("40%", "75%", "100%"),
                            listOf("40%", "75%", "100%").indexOf(renderScale)) { dialog, index ->
                            renderScale = listOf("40%", "75%", "100%")[index]
                            prefs.edit().putString("scale", renderScale).apply()
                            dialog.dismiss()
                            showInfo("Сохранено", "Выбрано $renderScale. Применение к игровому рендереру добавим после интеграции ядра.")
                        }.setNegativeButton("Отмена", null).show()
                    3 -> AlertDialog.Builder(this).setTitle("Версия Java")
                        .setSingleChoiceItems(arrayOf("8", "18", "21", "25"),
                            listOf("8", "18", "21", "25").indexOf(javaVersion)) { dialog, index ->
                            javaVersion = listOf("8", "18", "21", "25")[index]
                            prefs.edit().putString("java", javaVersion).apply()
                            dialog.dismiss()
                            showInfo("Сохранено", "Выбрана Java $javaVersion. Runtime ещё нужно будет установить.")
                        }.setNegativeButton("Отмена", null).show()
                    4 -> {
                        val input = EditText(this).apply {
                            setText(jvmArgs)
                            hint = "-Xmx2048M"
                        }
                        AlertDialog.Builder(this).setTitle("Аргументы JVM").setView(input)
                            .setPositiveButton("Сохранить") { _, _ ->
                                jvmArgs = input.text.toString()
                                prefs.edit().putString("jvm", jvmArgs).apply()
                            }.setNegativeButton("Отмена", null).show()
                    }
                    5 -> showInfo("Рендереры", "GL4ES, ANGLE и другие Android-совместимые варианты подключим при интеграции игрового ядра.")
                }
            }.setNegativeButton("Закрыть", null).show()
    }

    private fun pickBackground() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, 101)
    }

    @Deprecated("Legacy result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {}
                backgroundUri = uri
                prefs.edit().putString("background", uri.toString()).apply()
                applyBackground()
                status.text = "Фон выбран"
            }
        }
    }

    private fun applyBackground() {
        val uri = backgroundUri ?: return
        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null && ::root.isInitialized) {
                    root.background = BitmapDrawable(resources, bitmap)
                }
            }
        } catch (_: Exception) {
            if (::status.isInitialized) status.text = "Не удалось открыть фон"
        }
    }
}
