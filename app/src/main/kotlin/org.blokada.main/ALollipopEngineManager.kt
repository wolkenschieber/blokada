/* Copyright (C) 2017 Karsen Gauss <a@kar.gs>
 *
 * Derived from DNS66:
 * Copyright (C) 2016 Julian Andres Klode <jak@jak-linux.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Contributions shall also be provided under any later versions of the
 * GPL.
 */
package org.blokada.main

import android.annotation.TargetApi
import android.content.Context
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import gs.environment.inject
import nl.komponents.kovenant.any
import nl.komponents.kovenant.task
import org.blokada.property.IEngineManager
import org.blokada.property.State
import org.obsolete.KContext

@TargetApi(21)
class ALollipopEngineManager(
        private val ctx: Context,
        private val agent: ATunnelAgent,
        private val adBlocked: (String) -> Unit = {},
        private val error: (String) -> Unit = {},
        private val onRevoked: () -> Unit = {}
) : IEngineManager {

    private val s by lazy { ctx.inject().instance<State>() }
    private val waitKctx by lazy { ctx.inject().with("engineManagerWait").instance<KContext>() }
    private val events = ALollipopTunnelEvents(ctx, onRevoked)
    private var binder: ATunnelBinder? = null
    private var thread: TunnelThreadLollipopAndroid? = null

    @Synchronized override fun start() {
        val binding = agent.bind(events)
        binding.success {
            binder = it
            binder!!.actions.turnOn()
            thread = TunnelThreadLollipopAndroid(it.actions, s, adBlocked, error)
        }
        val wait = task(waitKctx) {
            Thread.sleep(3000)
        }
        any(listOf(binding, wait)).get()
        if (!binding.isSuccess()) throw Exception("could not bind to lollipop agent")
    }

    @Synchronized override fun updateFilters() {
        // Filters are fetched directly from the property
    }

    @Synchronized override fun stop() {
        thread?.stopThread()
        thread = null
        binder?.actions?.turnOff()
        agent.unbind()
        Thread.sleep(2000)
    }

}
