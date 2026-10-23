package com.gerasimov_mobile_dev.techstore.bridge

object WebScripts {
    //Скрыть шапку и поисковую строку сайта
    val hideHeaderAndSearch = """
    (function() {
        if (document.getElementById('app-header-hide-style')) return "ALREADY";
        var s = document.createElement('style');
        s.id = 'app-header-hide-style';
        s.textContent = "header.header-height-mobile { display: none !important; }";
        document.head.appendChild(s);
        return "INJECTED";
    })()
""".trimIndent()
    val hideBottomNavBar = """
    (function() {
        if (document.getElementById('app-hide-style')) return "ALREADY";
        var s = document.createElement('style');
        s.id = 'app-hide-style';
        s.textContent = ".main-floating-menu { display: none !important; }";
        document.head.appendChild(s);
        return "INJECTED";
    })()
""".trimIndent()


    //Еще один скрипт для скрытия шапки и навигации
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


    //Если сайт динамический и шапка подгружается асинхронно (скрипт проверяет наличие элемента каждые 100мс, пока не найдет)
    val hideHeaderAdvanced = """
    (function() {
        function hide() {
            const header = document.querySelector('header-wow-top');
            if (header) {
                header.style.display = 'none';
                const main = document.querySelector('main');
                if (main) {
                    main.style.marginTop = '0';
                    main.style.paddingTop = '0';
                }
                return true; // Нашли и скрыли
            }
            return false; // Не нашли
        }

        if (!hide()) {
            const interval = setInterval(() => {
                if (hide()) clearInterval(interval);
            }, 100);
        }
    })();
""".trimIndent()
        //
    fun siteSearch(query: String): String {
        // Экранируем кавычки, бэкслеши и переводы строк, чтобы запрос не сломал JS
        val safeQuery = query
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")

        return """
            (function() {
                const input = document.getElementById('search-input');
                if (!input) return;

                input.value = "$safeQuery";

                // catalog.js слушает событие 'input' — диспатчим его, чтобы фильтр сработал
                input.dispatchEvent(new Event('input', { bubbles: true }));
            })();
        """.trimIndent()
    }
}