package com.gerasimov_mobile_dev.techstore.bridge

object WebScripts {
    //Скрыть шапку и поисковую строку сайта
    val hideHeaderAndSearch = """
        (function() {
            const el = document.querySelector('header');
            if (el) el.style.display = 'none';
            
            const searchBar = document.querySelector('.search-bar');
            if (searchBar) searchBar.style.display = 'none'; 
            
            const main = document.querySelector('main');
            if (main) {
                main.style.marginTop = '0';
                main.style.paddingTop = '0';
            }
        })();
    """.trimIndent()

    //Если сайт динамический и шапка подгружается асинхронно (скрипт проверяет наличие элемента каждые 100мс, пока не найдет)
    val hideHeaderAdvanced = """
    (function() {
        function hide() {
            const header = document.querySelector('header');
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