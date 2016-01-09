/*!
 * Star Rating <LANG> Translations
 *
 * This file must be loaded after 'fileinput.js'. Patterns in braces '{}', or
 * any HTML markup tags in the messages must not be converted or translated.
 *
 * @see http://github.com/kartik-v/bootstrap-star-rating
 * @author Kartik Visweswaran <kartikv2@gmail.com>
 *
 * NOTE: this file must be saved in UTF-8 encoding.
 */
(function ($) {
    "use strict";
    $.fn.ratingLocales['br'] = {
        defaultCaption: '{rating} Estrelas',
        starCaptions: {
            0.5: 'Meia Estrela',
            1: 'Uma Estrela',
            1.5: 'Uma Estrela e Meia',
            2: 'Duas Estrelas',
            2.5: 'Duas Estrelas e Meia',
            3: 'Três Estrelas',
            3.5: 'Três Estrelas e meia',
            4: 'Quatro Estrelas',
            4.5: 'Quatro Estrelas e Meia',
            5: 'Cinco Estrelas'
        },
        clearButtonTitle: 'Limpar',
        clearCaption: 'Não avaliado'
    };
})(window.jQuery);
