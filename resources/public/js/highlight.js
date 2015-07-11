$(function() {
  function highlightHashLine() {
    var hash = window.location.hash;

    var $highlighted = $('.highlighted-line');
    if ($highlighted) {
      $highlighted.removeClass('highlighted-line');
    }

    if (hash) {
      hash = hash.substr(1);
      $('a[name="' + hash + '"]')
      .parents('.line')
      .addClass('highlighted-line')
    }
  }

  $(window).on('hashchange', highlightHashLine);

  highlightHashLine();
});
