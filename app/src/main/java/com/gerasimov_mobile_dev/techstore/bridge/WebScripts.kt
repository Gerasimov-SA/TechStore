package com.gerasimov_mobile_dev.techstore.bridge

object WebScripts {

    //Скрипт для скрытия шапки и навигации веб-сайта
    val injectHideStyles = """
    (function() {
        if (!document.getElementById('app-hide-style')) {
            var s = document.createElement('style');
            s.id = 'app-hide-style';
            s.textContent = 'header.header-height-mobile, .main-floating-menu { display: none !important; }';
            (document.head || document.documentElement).appendChild(s);
        }
        return "INJECTED";
    })()
""".trimIndent()

    //Скрипт для проверки что шапка и навигация скрыты
    val verifyHidden = """
    (function() {
        return document.getElementById('app-hide-style') ? "OK" : "PENDING";
    })()
""".trimIndent()


    //Скрипт для ввода текста прямо в поле поиска веб-сайта
    fun typeInSiteSearch(query: String): String {
        val safe = query
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")

        return """
        (function() {
            var input =
                document.querySelector('header input[type="search"]') ||
                document.querySelector('input[name="search"]') ||
                document.querySelector('input[placeholder*="Поиск"]') ||
                document.querySelector('input[placeholder*="поиск"]');
            if (!input) return "NO_INPUT";

            var proto = Object.getOwnPropertyDescriptor(
                window.HTMLInputElement.prototype, 'value'
            );
            if (proto && proto.set) proto.set.call(input, "$safe");
            else input.value = "$safe";

            input.dispatchEvent(new Event('input', { bubbles: true }));
            return "TYPED";
        })();
    """.trimIndent()
    }

    //Скрипт для чтения подсказок поиска из веб-сайта
    val readSearchSuggestions = """
    (function() {
        var seen = {};
        var out = [];
        var nodes = document.querySelectorAll('.live-search-results-item[data-query]');
        for (var i = 0; i < nodes.length && out.length < 6; i++) {
            var q = nodes[i].getAttribute('data-query');
            if (q && !seen[q]) { seen[q] = true; out.push(q); }
        }
        return out;
    })()
""".trimIndent()
}