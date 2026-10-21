package com.gerasimov_mobile_dev.techstore.bridge

object WebScripts {
    //Скрыть шапку сайта
    val hideHeader = """
        (function() {
            const el = document.querySelector('header');
            if (el) el.style.display = 'none';
            
            const main = document.querySelector('main');
            if (main) {
                main.style.marginTop = '0';
                main.style.paddingTop = '0';
            }
        })();
    """.trimIndent()

}