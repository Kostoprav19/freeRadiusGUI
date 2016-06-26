/**
 * Created by Dan on 26.06.2016.
 */
$(function () {
    $( '#table' ).searchable({
        striped: true,
        oddRow: { 'background-color': '#fff' },
        evenRow: { 'background-color': '#f5f5f5' },
        searchType: 'fuzzy'
    });

    $( '#searchable-container' ).searchable({
        searchField: '#container-search',
        selector: '.row',
        childSelector: '.col-xs-4',
        show: function( elem ) {
            elem.slideDown(100);
        },
        hide: function( elem ) {
            elem.slideUp( 100 );
        }
    })
});