package org.blokada.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.salomonbrys.kodein.instance
import gs.environment.Environment
import gs.environment.inject
import gs.presentation.WebViewActor
import gs.property.IProperty
import org.blokada.R
import org.blokada.property.Dash
import org.blokada.property.Pages
import org.blokada.property.State
import org.obsolete.IWhen
import java.net.URL

val DASH_ID_DONATE = "main_donate"
val DASH_ID_CONTRIBUTE = "main_contribute"
val DASH_ID_BLOG = "main_blog"
val DASH_ID_FAQ = "main_faq"
val DASH_ID_FEEDBACK = "main_feedback"
val DASH_ID_PATRON = "main_patron"
val DASH_ID_PATRON_ABOUT = "main_patron_about"
val DASH_ID_CHANGELOG = "main_changelog"

class DonateDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_DONATE,
        R.drawable.ic_heart_box,
        text = ctx.getString(R.string.main_donate_text),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.donate)),
        hasView = true
) {

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup,
                false)
        val actor = WebViewActor(view, pages.donate, javascript = true)
        onBack = { actor.reload() }
        return view
    }
}

class ContributeDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_CONTRIBUTE,
        R.drawable.ic_code_tags,
        text = ctx.getString(R.string.main_contribute_text),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.contribute)),
        hasView = true
) {

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup,
                false)
        val actor = WebViewActor(view, pages.contribute)
        onBack = { actor.reload() }
        return view
    }
}

class BlogDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_BLOG,
        R.drawable.ic_comment_multiple_outline,
        text = ctx.getString(R.string.main_blog_text),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.community)),
        hasView = true
) {

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup,
                false)
        val actor = WebViewActor(view, pages.community, forceEmbedded = true, javascript = true)
        onBack = { actor.reload() }
        return view
    }
}

class FaqDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_FAQ,
        R.drawable.ic_help_outline,
        text = ctx.getString(R.string.main_faq_text),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.help)),
        hasView = true
) {

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup,
                false)
        val actor = WebViewActor(view, pages.help)
        onBack = { actor.reload() }
        return view
    }
}

class FeedbackDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_FEEDBACK,
        R.drawable.ic_feedback,
        text = ctx.getString(R.string.main_feedback_text),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.feedback)),
        hasView = true
) {
    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup, false)
        val actor = WebViewActor(view, pages.feedback, forceEmbedded = true, javascript = true)
        onBack = { actor.reload() }
        return view
    }
}

class PatronDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_PATRON,
        R.drawable.ic_info,
        text = ctx.getString(R.string.main_patron),
        hasView = true,
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.patron))
) {
    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup, false)
        val actor = WebViewActor(view, pages.patron, forceEmbedded = true, javascript = true)
        onBack = { actor.reload() }
        return view
    }
}

class PatronAboutDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_PATRON_ABOUT,
        R.drawable.ic_info,
        text = ctx.getString(R.string.main_patron_about),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.patronAbout)),
        hasView = true
) {
    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup, false)
        val actor = WebViewActor(view, pages.patronAbout, forceEmbedded = true, javascript = true)
        onBack = { actor.reload() }
        return view
    }
}

class ChangelogDash(
        val xx: Environment,
        val ctx: Context = xx().instance(),
        val pages: Pages = xx().instance()
) : Dash(
        DASH_ID_CHANGELOG,
        R.drawable.ic_info,
        text = ctx.getString(R.string.main_changelog),
        menuDashes = Triple(null, null, OpenInBrowserDash(ctx, pages.changelog)),
        hasView = true
) {
    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.content_webview, parent as ViewGroup, false)
        val actor = WebViewActor(view, pages.changelog)
        onBack = { actor.reload() }
        return view
    }
}

class AutoStartDash(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "main_autostart",
        icon = false,
        text = ctx.getString(R.string.main_autostart_text),
        isSwitch = true
) {

    override var checked = false
        set(value) { if (field != value) {
            field = value
            s.startOnBoot %= value
            onUpdate.forEach { it() }
        }}

    private var listener: IWhen? = null
    init {
        listener = s.startOnBoot.doOnUiWhenSet().then {
            checked = s.startOnBoot()
        }
    }
}

class ConnectivityDash(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "main_connectivity",
        icon = false,
        text = ctx.getString(R.string.main_connectivity_text),
        isSwitch = true
) {

    override var checked = false
        set(value) { if (field != value) {
            field = value
            s.watchdogOn %= value
            onUpdate.forEach { it() }
        }}

    private var listener: IWhen? = null
    init {
        listener = s.watchdogOn.doOnUiWhenSet().then {
            checked = s.watchdogOn()
        }
    }
}

class OpenInBrowserDash(
        val ctx: Context,
        val url: IProperty<URL>
) : Dash(
        "open_in_browser",
        R.drawable.ic_open_in_new,
        onClick = { dashRef ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setData(Uri.parse(url().toString()))
            ctx.startActivity(intent)
            true
        }
)

class ChatDash(
        val ctx: Context,
        val url: IProperty<URL>
) : Dash(
        "chat",
        R.drawable.ic_comment_multiple_outline,
        onClick = { dashRef ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setData(Uri.parse(url().toString()))
            ctx.startActivity(intent)
            true
        }
)
