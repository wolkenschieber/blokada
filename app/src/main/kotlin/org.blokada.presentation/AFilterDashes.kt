package org.blokada.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.with
import gs.environment.ActivityProvider
import gs.environment.inject
import nl.komponents.kovenant.task
import org.blokada.R
import org.blokada.main.MainActivity
import org.blokada.property.Dash
import org.blokada.property.Filter
import org.blokada.property.State
import org.blokada.property.UiState
import org.obsolete.IWhen

val DASH_ID_BLACKLIST = "filter_blacklist"
val DASH_ID_WHITELIST = "filter_whitelist"

private val KCTX = "filter-dashes"

class DashFilterBlacklist(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(DASH_ID_BLACKLIST,
        R.drawable.ic_shield_outline,
        text = ctx.getString(R.string.filter_blacklist_text_none),
        menuDashes = Triple(AddBlacklist(ctx, s), GenerateBlacklist(ctx, s), null),
        onDashOpen = { task(ctx.inject().with(KCTX).instance()) {
            var changed = false
            s.filters().filter { !it.whitelist }.forEach {
                if (it.hosts.isEmpty()) {
                    it.hosts = it.source.fetch()
                    changed = true
                }
            }
            if (changed) s.filters %= s.filters()
        }},
        hasView = true
) {
    private var listener: IWhen? = null

    init {
        listener = s.filters.doOnUiWhenSet().then {
            update(s.filters().filter { it.active && !it.whitelist })
        }
    }

    private fun update(filters: List<Filter>) {
        if (filters.isEmpty()) text = ctx.getString(R.string.filter_blacklist_text_none)
        else text = ctx.resources.getString(R.string.filter_blacklist_text, filters.size)
    }

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.view_customlist, parent as ViewGroup, false)
        if (view is AFilterListView) {
            val activity: ActivityProvider<MainActivity> = ctx.inject().instance()
            view.landscape = activity.get()?.landscape ?: false
            view.whitelist = false
        }
        return view
    }
}

class DashFilterWhitelist(
        val ctx: Context,
        val s: State = ctx.inject().instance(),
        val ui: UiState = ctx.inject().instance()
) : Dash(DASH_ID_WHITELIST,
        R.drawable.ic_verified,
        text = ctx.getString(R.string.filter_whitelist_text_none),
        menuDashes = Triple(
                AddWhitelist(ctx, s), GenerateWhitelist(ctx, s), ShowSystemAppsWhitelist(ctx, ui)
        ),
        onDashOpen = { task(ctx.inject().with(KCTX).instance()) {
            var changed = false
            s.filters().filter { it.whitelist }.forEach {
                if (it.hosts.isEmpty()) {
                    it.hosts = it.source.fetch()
                    changed = true
                }
            }
            if (changed) s.filters %= s.filters()
        }},
        hasView = true
) {

    private var listener: IWhen? = null

    init {
        listener = s.filters.doOnUiWhenSet().then {
            update(s.filters().filter { it.active && it.whitelist })
        }
    }

    private fun update(filters: List<Filter>) {
        if (filters.isEmpty()) text = ctx.getString(R.string.filter_whitelist_text_none)
        else text = ctx.resources.getString(R.string.filter_whitelist_text, filters.size)
    }

    override fun createView(parent: Any): Any? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.view_customlist, parent as ViewGroup, false)
        if (view is AFilterListView) {
            val activity: ActivityProvider<MainActivity> = ctx.inject().instance()
            view.landscape = activity.get()?.landscape ?: false
            view.whitelist = true
        }
        return view
    }
}

class AddBlacklist(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "filter_blacklist_add",
        R.drawable.ic_filter_add,
        onClick = {
            val dialogProvider: () -> AFilterAddDialog = ctx.inject().provider()
            val dialog: AFilterAddDialog = dialogProvider()
            dialog.onSave = { newFilter ->
                newFilter.whitelist = false
                s.filters %= s.filters() + newFilter
            }
            dialog.show(null, whitelist = false)
            false
        }
)

class AddWhitelist(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "filter_whitelist_add",
        R.drawable.ic_filter_add,
        onClick = {
            val dialogProvider: () -> AFilterAddDialog = ctx.inject().provider()
            val dialog: AFilterAddDialog = dialogProvider()
            dialog.onSave = { newFilter ->
                newFilter.whitelist = true
                s.filters %= s.filters() + newFilter
            }
            dialog.show(null, whitelist = true)
            false
        }
)

class GenerateWhitelist(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "filter_whitelist_generate",
        R.drawable.ic_tune,
        onClick = {
            val dialogProvider: () -> AFilterGenerateDialog = ctx.inject().provider(true)
            val dialog: AFilterGenerateDialog = dialogProvider()
            dialog.show()
            false
        }
)

class GenerateBlacklist(
        val ctx: Context,
        val s: State = ctx.inject().instance()
) : Dash(
        "filter_blacklist_generate",
        R.drawable.ic_tune,
        onClick = {
            val dialogProvider: () -> AFilterGenerateDialog = ctx.inject().provider(false)
            val dialog: AFilterGenerateDialog = dialogProvider()
            dialog.show()
            false
        }
)

class ShowSystemAppsWhitelist(
        val ctx: Context,
        val ui: UiState = ctx.inject().instance()
) : Dash(
        "filter_whitelist_showsystem",
        icon = false,
        isSwitch = true
) {
    override var checked = false
        set(value) { if (field != value) {
            field = value
            ui.showSystemApps %= value
            onUpdate.forEach { it() }
        }}

    private val listener: Any
    init {
        listener = ui.showSystemApps.doOnUiWhenSet().then {
            checked = ui.showSystemApps()
        }
    }
}
