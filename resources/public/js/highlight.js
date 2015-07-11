$(function() {
  function highlightHashLine() {
    var hash = window.location.hash;

    var $highlightedLine = $('.highlighted-line');
    if ($highlightedLine) {
      $highlightedLine.removeClass('highlighted-line');
    }

    var $highlightedSymbols = $('.highlighted-symbol');
    if ($highlightedSymbols) {
      $highlightedSymbols.removeClass('highlighted-symbol');
    }

    if (hash) {
      hash = hash.substr(1);
      $def = $('.def[data-symbol="' + hash + '"]');

      $def.parents('.line').addClass('highlighted-line');
      $('[data-symbol="' + hash + '"]').addClass('highlighted-symbol');
    }
  }

  $(window).on('hashchange', highlightHashLine);

  highlightHashLine();

  $('.def').on('click', function() {
    var sym = $(this).data('symbol');
    window.location.hash = '#' + sym;
  });
});
