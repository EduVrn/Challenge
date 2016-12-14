/*!
 * imagesLoaded PACKAGED v4.1.1
 * JavaScript is all like "You images are done yet or what?"
 * MIT License
 */

!function (t, e) {
    "function" == typeof define && define.amd ? define("ev-emitter/ev-emitter", e) : "object" == typeof module && module.exports ? module.exports = e() : t.EvEmitter = e()
}("undefined" != typeof window ? window : this, function () {
    function t() {}
    var e = t.prototype;
    return e.on = function (t, e) {
        if (t && e) {
            var i = this._events = this._events || {}, n = i[t] = i[t] || [];
            return-1 == n.indexOf(e) && n.push(e), this
        }
    }, e.once = function (t, e) {
        if (t && e) {
            this.on(t, e);
            var i = this._onceEvents = this._onceEvents || {}, n = i[t] = i[t] || {};
            return n[e] = !0, this
        }
    }, e.off = function (t, e) {
        var i = this._events && this._events[t];
        if (i && i.length) {
            var n = i.indexOf(e);
            return-1 != n && i.splice(n, 1), this
        }
    }, e.emitEvent = function (t, e) {
        var i = this._events && this._events[t];
        if (i && i.length) {
            var n = 0, o = i[n];
            e = e || [];
            for (var r = this._onceEvents && this._onceEvents[t]; o; ) {
                var s = r && r[o];
                s && (this.off(t, o), delete r[o]), o.apply(this, e), n += s ? 0 : 1, o = i[n]
            }
            return this
        }
    }, t
}), function (t, e) {
    "use strict";
    "function" == typeof define && define.amd ? define(["ev-emitter/ev-emitter"], function (i) {
        return e(t, i)
    }) : "object" == typeof module && module.exports ? module.exports = e(t, require("ev-emitter")) : t.imagesLoaded = e(t, t.EvEmitter)
}(window, function (t, e) {
    function i(t, e) {
        for (var i in e)
            t[i] = e[i];
        return t
    }
    function n(t) {
        var e = [];
        if (Array.isArray(t))
            e = t;
        else if ("number" == typeof t.length)
            for (var i = 0; i < t.length; i++)
                e.push(t[i]);
        else
            e.push(t);
        return e
    }
    function o(t, e, r) {
        return this instanceof o ? ("string" == typeof t && (t = document.querySelectorAll(t)), this.elements = n(t), this.options = i({}, this.options), "function" == typeof e ? r = e : i(this.options, e), r && this.on("always", r), this.getImages(), h && (this.jqDeferred = new h.Deferred), void setTimeout(function () {
            this.check()
        }.bind(this))) : new o(t, e, r)
    }
    function r(t) {
        this.img = t
    }
    function s(t, e) {
        this.url = t, this.element = e, this.img = new Image
    }
    var h = t.jQuery, a = t.console;
    o.prototype = Object.create(e.prototype), o.prototype.options = {}, o.prototype.getImages = function () {
        this.images = [], this.elements.forEach(this.addElementImages, this)
    }, o.prototype.addElementImages = function (t) {
        "IMG" == t.nodeName && this.addImage(t), this.options.background === !0 && this.addElementBackgroundImages(t);
        var e = t.nodeType;
        if (e && d[e]) {
            for (var i = t.querySelectorAll("img"), n = 0; n < i.length; n++) {
                var o = i[n];
                this.addImage(o)
            }
            if ("string" == typeof this.options.background) {
                var r = t.querySelectorAll(this.options.background);
                for (n = 0; n < r.length; n++) {
                    var s = r[n];
                    this.addElementBackgroundImages(s)
                }
            }
        }
    };
    var d = {1: !0, 9: !0, 11: !0};
    return o.prototype.addElementBackgroundImages = function (t) {
        var e = getComputedStyle(t);
        if (e)
            for (var i = /url\((['"])?(.*?)\1\)/gi, n = i.exec(e.backgroundImage); null !== n; ) {
                var o = n && n[2];
                o && this.addBackground(o, t), n = i.exec(e.backgroundImage)
            }
    }, o.prototype.addImage = function (t) {
        var e = new r(t);
        this.images.push(e)
    }, o.prototype.addBackground = function (t, e) {
        var i = new s(t, e);
        this.images.push(i)
    }, o.prototype.check = function () {
        function t(t, i, n) {
            setTimeout(function () {
                e.progress(t, i, n)
            })
        }
        var e = this;
        return this.progressedCount = 0, this.hasAnyBroken = !1, this.images.length ? void this.images.forEach(function (e) {
            e.once("progress", t), e.check()
        }) : void this.complete()
    }, o.prototype.progress = function (t, e, i) {
        this.progressedCount++, this.hasAnyBroken = this.hasAnyBroken || !t.isLoaded, this.emitEvent("progress", [this, t, e]), this.jqDeferred && this.jqDeferred.notify && this.jqDeferred.notify(this, t), this.progressedCount == this.images.length && this.complete(), this.options.debug && a && a.log("progress: " + i, t, e)
    }, o.prototype.complete = function () {
        var t = this.hasAnyBroken ? "fail" : "done";
        if (this.isComplete = !0, this.emitEvent(t, [this]), this.emitEvent("always", [this]), this.jqDeferred) {
            var e = this.hasAnyBroken ? "reject" : "resolve";
            this.jqDeferred[e](this)
        }
    }, r.prototype = Object.create(e.prototype), r.prototype.check = function () {
        var t = this.getIsImageComplete();
        return t ? void this.confirm(0 !== this.img.naturalWidth, "naturalWidth") : (this.proxyImage = new Image, this.proxyImage.addEventListener("load", this), this.proxyImage.addEventListener("error", this), this.img.addEventListener("load", this), this.img.addEventListener("error", this), void(this.proxyImage.src = this.img.src))
    }, r.prototype.getIsImageComplete = function () {
        return this.img.complete && void 0 !== this.img.naturalWidth
    }, r.prototype.confirm = function (t, e) {
        this.isLoaded = t, this.emitEvent("progress", [this, this.img, e])
    }, r.prototype.handleEvent = function (t) {
        var e = "on" + t.type;
        this[e] && this[e](t)
    }, r.prototype.onload = function () {
        this.confirm(!0, "onload"), this.unbindEvents()
    }, r.prototype.onerror = function () {
        this.confirm(!1, "onerror"), this.unbindEvents()
    }, r.prototype.unbindEvents = function () {
        this.proxyImage.removeEventListener("load", this), this.proxyImage.removeEventListener("error", this), this.img.removeEventListener("load", this), this.img.removeEventListener("error", this)
    }, s.prototype = Object.create(r.prototype), s.prototype.check = function () {
        this.img.addEventListener("load", this), this.img.addEventListener("error", this), this.img.src = this.url;
        var t = this.getIsImageComplete();
        t && (this.confirm(0 !== this.img.naturalWidth, "naturalWidth"), this.unbindEvents())
    }, s.prototype.unbindEvents = function () {
        this.img.removeEventListener("load", this), this.img.removeEventListener("error", this)
    }, s.prototype.confirm = function (t, e) {
        this.isLoaded = t, this.emitEvent("progress", [this, this.element, e])
    }, o.makeJQueryPlugin = function (e) {
        e = e || t.jQuery, e && (h = e, h.fn.imagesLoaded = function (t, e) {
            var i = new o(this, t, e);
            return i.jqDeferred.promise(h(this))
        })
    }, o.makeJQueryPlugin(), o
});



/*
 * jQuery Superfish Menu Plugin
 * Copyright (c) 2013 Joel Birch
 *
 * Dual licensed under the MIT and GPL licenses:
 *  http://www.opensource.org/licenses/mit-license.php
 *  http://www.gnu.org/licenses/gpl.html
 */

(function ($) {
    "use strict";

    var methods = (function () {
        // private properties and methods go here
        var c = {
            bcClass: 'sf-breadcrumb',
            menuClass: 'sf-js-enabled',
            anchorClass: 'sf-with-ul',
            menuArrowClass: 'sf-arrows'
        },
                ios = (function () {
                    var ios = /iPhone|iPad|iPod/i.test(navigator.userAgent);
                    if (ios) {
                        // iOS clicks only bubble as far as body children
                        $(window).load(function () {
                            $('body').children().on('click', $.noop);
                        });
                    }
                    return ios;
                })(),
                wp7 = (function () {
                    var style = document.documentElement.style;
                    return ('behavior' in style && 'fill' in style && /iemobile/i.test(navigator.userAgent));
                })(),
                toggleMenuClasses = function ($menu, o) {
                    var classes = c.menuClass;
                    if (o.cssArrows) {
                        classes += ' ' + c.menuArrowClass;
                    }
                    $menu.toggleClass(classes);
                },
                setPathToCurrent = function ($menu, o) {
                    return $menu.find('li.' + o.pathClass).slice(0, o.pathLevels)
                            .addClass(o.hoverClass + ' ' + c.bcClass)
                            .filter(function () {
                                return ($(this).children(o.popUpSelector).hide().show().length);
                            }).removeClass(o.pathClass);
                },
                toggleAnchorClass = function ($li) {
                    $li.children('a').toggleClass(c.anchorClass);
                },
                toggleTouchAction = function ($menu) {
                    var touchAction = $menu.css('ms-touch-action');
                    touchAction = (touchAction === 'pan-y') ? 'auto' : 'pan-y';
                    $menu.css('ms-touch-action', touchAction);
                },
                applyHandlers = function ($menu, o) {
                    var targets = 'li:has(' + o.popUpSelector + ')';
                    if ($.fn.hoverIntent && !o.disableHI) {
                        $menu.hoverIntent(over, out, targets);
                    } else {
                        $menu
                                .on('mouseenter.superfish', targets, over)
                                .on('mouseleave.superfish', targets, out);
                    }
                    var touchevent = 'MSPointerDown.superfish';
                    if (!ios) {
                        touchevent += ' touchend.superfish';
                    }
                    if (wp7) {
                        touchevent += ' mousedown.superfish';
                    }
                    $menu
                            .on('focusin.superfish', 'li', over)
                            .on('focusout.superfish', 'li', out)
                            .on(touchevent, 'a', o, touchHandler);
                },
                touchHandler = function (e) {
                    var $this = $(this),
                            $ul = $this.siblings(e.data.popUpSelector);

                    if ($ul.length > 0 && $ul.is(':hidden')) {
                        $this.one('click.superfish', false);
                        if (e.type === 'MSPointerDown') {
                            $this.trigger('focus');
                        } else {
                            $.proxy(over, $this.parent('li'))();
                        }
                    }
                },
                over = function () {
                    var $this = $(this),
                            o = getOptions($this);
                    clearTimeout(o.sfTimer);
                    $this.siblings().superfish('hide').end().superfish('show');
                },
                out = function () {
                    var $this = $(this),
                            o = getOptions($this);
                    if (ios) {
                        $.proxy(close, $this, o)();
                    } else {
                        clearTimeout(o.sfTimer);
                        o.sfTimer = setTimeout($.proxy(close, $this, o), o.delay);
                    }
                },
                close = function (o) {
                    o.retainPath = ($.inArray(this[0], o.$path) > -1);
                    this.superfish('hide');

                    if (!this.parents('.' + o.hoverClass).length) {
                        o.onIdle.call(getMenu(this));
                        if (o.$path.length) {
                            $.proxy(over, o.$path)();
                        }
                    }
                },
                getMenu = function ($el) {
                    return $el.closest('.' + c.menuClass);
                },
                getOptions = function ($el) {
                    return getMenu($el).data('sf-options');
                };

        return {
            // public methods
            hide: function (instant) {
                if (this.length) {
                    var $this = this,
                            o = getOptions($this);
                    if (!o) {
                        return this;
                    }
                    var not = (o.retainPath === true) ? o.$path : '',
                            $ul = $this.find('li.' + o.hoverClass).add(this).not(not).removeClass(o.hoverClass).children(o.popUpSelector),
                            speed = o.speedOut;

                    if (instant) {
                        $ul.show();
                        speed = 0;
                    }
                    o.retainPath = false;
                    o.onBeforeHide.call($ul);
                    $ul.stop(true, true).animate(o.animationOut, speed, function () {
                        var $this = $(this);
                        o.onHide.call($this);
                    });
                }
                return this;
            },
            show: function () {
                var o = getOptions(this);
                if (!o) {
                    return this;
                }
                var $this = this.addClass(o.hoverClass),
                        $ul = $this.children(o.popUpSelector);

                o.onBeforeShow.call($ul);
                $ul.stop(true, true).animate(o.animation, o.speed, function () {
                    o.onShow.call($ul);
                });
                return this;
            },
            destroy: function () {
                return this.each(function () {
                    var $this = $(this),
                            o = $this.data('sf-options'),
                            $hasPopUp;
                    if (!o) {
                        return false;
                    }
                    $hasPopUp = $this.find(o.popUpSelector).parent('li');
                    clearTimeout(o.sfTimer);
                    toggleMenuClasses($this, o);
                    toggleAnchorClass($hasPopUp);
                    toggleTouchAction($this);
                    // remove event handlers
                    $this.off('.superfish').off('.hoverIntent');
                    // clear animation's inline display style
                    $hasPopUp.children(o.popUpSelector).attr('style', function (i, style) {
                        return style.replace(/display[^;]+;?/g, '');
                    });
                    // reset 'current' path classes
                    o.$path.removeClass(o.hoverClass + ' ' + c.bcClass).addClass(o.pathClass);
                    $this.find('.' + o.hoverClass).removeClass(o.hoverClass);
                    o.onDestroy.call($this);
                    $this.removeData('sf-options');
                });
            },
            init: function (op) {
                return this.each(function () {
                    var $this = $(this);
                    if ($this.data('sf-options')) {
                        return false;
                    }
                    var o = $.extend({}, $.fn.superfish.defaults, op),
                            $hasPopUp = $this.find(o.popUpSelector).parent('li');
                    o.$path = setPathToCurrent($this, o);

                    $this.data('sf-options', o);

                    toggleMenuClasses($this, o);
                    toggleAnchorClass($hasPopUp);
                    toggleTouchAction($this);
                    applyHandlers($this, o);

                    $hasPopUp.not('.' + c.bcClass).superfish('hide', true);

                    o.onInit.call(this);
                });
            }
        };
    })();

    $.fn.superfish = function (method, args) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            return $.error('Method ' + method + ' does not exist on jQuery.fn.superfish');
        }
    };

    $.fn.superfish.defaults = {
        popUpSelector: 'ul,.sf-mega', // within menu context
        hoverClass: 'sfHover',
        pathClass: 'overrideThisToUse',
        pathLevels: 1,
        delay: 800,
        animation: {opacity: 'show'},
        animationOut: {opacity: 'hide'},
        speed: 'normal',
        speedOut: 'fast',
        cssArrows: true,
        disableHI: false,
        onInit: $.noop,
        onBeforeShow: $.noop,
        onShow: $.noop,
        onBeforeHide: $.noop,
        onHide: $.noop,
        onIdle: $.noop,
        onDestroy: $.noop
    };

    // soon to be deprecated
    $.fn.extend({
        hideSuperfishUl: methods.hide,
        showSuperfishUl: methods.show
    });

})(jQuery);












/*
 * Swiper 2.6.0
 * Mobile touch slider and framework with hardware accelerated transitions
 *
 * http://www.idangero.us/sliders/swiper/
 *
 * Copyright 2010-2014, Vladimir Kharlampidi
 * The iDangero.us
 * http://www.idangero.us/
 *
 * Licensed under GPL & MIT
 *
 * Released on: April 9, 2014
 */
var Swiper = function (selector, params) {
    'use strict';

    /*=========================
     A little bit dirty but required part for IE8 and old FF support
     ===========================*/
    if (document.body.__defineGetter__) {
        if (HTMLElement) {
            var element = HTMLElement.prototype;
            if (element.__defineGetter__) {
                element.__defineGetter__('outerHTML', function () {
                    return new XMLSerializer().serializeToString(this);
                });
            }
        }
    }

    if (!window.getComputedStyle) {
        window.getComputedStyle = function (el, pseudo) {
            this.el = el;
            this.getPropertyValue = function (prop) {
                var re = /(\-([a-z]){1})/g;
                if (prop === 'float')
                    prop = 'styleFloat';
                if (re.test(prop)) {
                    prop = prop.replace(re, function () {
                        return arguments[2].toUpperCase();
                    });
                }
                return el.currentStyle[prop] ? el.currentStyle[prop] : null;
            };
            return this;
        };
    }
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function (obj, start) {
            for (var i = (start || 0), j = this.length; i < j; i++) {
                if (this[i] === obj) {
                    return i;
                }
            }
            return -1;
        };
    }
    if (!document.querySelectorAll) {
        if (!window.jQuery)
            return;
    }
    function $$(selector, context) {
        if (document.querySelectorAll)
            return (context || document).querySelectorAll(selector);
        else
            return jQuery(selector, context);
    }

    /*=========================
     Check for correct selector
     ===========================*/
    if (typeof selector === 'undefined')
        return;

    if (!(selector.nodeType)) {
        if ($$(selector).length === 0)
            return;
    }

    /*=========================
     _this
     ===========================*/
    var _this = this;

    /*=========================
     Default Flags and vars
     ===========================*/
    _this.touches = {
        start: 0,
        startX: 0,
        startY: 0,
        current: 0,
        currentX: 0,
        currentY: 0,
        diff: 0,
        abs: 0
    };
    _this.positions = {
        start: 0,
        abs: 0,
        diff: 0,
        current: 0
    };
    _this.times = {
        start: 0,
        end: 0
    };

    _this.id = (new Date()).getTime();
    _this.container = (selector.nodeType) ? selector : $$(selector)[0];
    _this.isTouched = false;
    _this.isMoved = false;
    _this.activeIndex = 0;
    _this.centerIndex = 0;
    _this.activeLoaderIndex = 0;
    _this.activeLoopIndex = 0;
    _this.previousIndex = null;
    _this.velocity = 0;
    _this.snapGrid = [];
    _this.slidesGrid = [];
    _this.imagesToLoad = [];
    _this.imagesLoaded = 0;
    _this.wrapperLeft = 0;
    _this.wrapperRight = 0;
    _this.wrapperTop = 0;
    _this.wrapperBottom = 0;
    _this.isAndroid = navigator.userAgent.toLowerCase().indexOf('android') >= 0;
    var wrapper, slideSize, wrapperSize, direction, isScrolling, containerSize;

    /*=========================
     Default Parameters
     ===========================*/
    var defaults = {
        eventTarget: 'wrapper', // or 'container'
        mode: 'horizontal', // or 'vertical'
        touchRatio: 1,
        speed: 300,
        freeMode: false,
        freeModeFluid: false,
        momentumRatio: 1,
        momentumBounce: true,
        momentumBounceRatio: 1,
        slidesPerView: 1,
        slidesPerGroup: 1,
        slidesPerViewFit: true, //Fit to slide when spv "auto" and slides larger than container
        simulateTouch: true,
        followFinger: true,
        shortSwipes: true,
        longSwipesRatio: 0.5,
        moveStartThreshold: false,
        onlyExternal: false,
        createPagination: true,
        pagination: false,
        paginationElement: 'span',
        paginationClickable: false,
        paginationAsRange: true,
        resistance: true, // or false or 100%
        scrollContainer: false,
        preventLinks: true,
        preventLinksPropagation: false,
        noSwiping: false, // or class
        noSwipingClass: 'swiper-no-swiping', //:)
        initialSlide: 0,
        keyboardControl: false,
        mousewheelControl: false,
        mousewheelControlForceToAxis: false,
        useCSS3Transforms: true,
        // Autoplay
        autoplay: false,
        autoplayDisableOnInteraction: true,
        autoplayStopOnLast: false,
        //Loop mode
        loop: false,
        loopAdditionalSlides: 0,
        // Round length values
        roundLengths: false,
        //Auto Height
        calculateHeight: false,
        cssWidthAndHeight: false,
        //Images Preloader
        updateOnImagesReady: true,
        //Form elements
        releaseFormElements: true,
        //Watch for active slide, useful when use effects on different slide states
        watchActiveIndex: false,
        //Slides Visibility Fit
        visibilityFullFit: false,
        //Slides Offset
        offsetPxBefore: 0,
        offsetPxAfter: 0,
        offsetSlidesBefore: 0,
        offsetSlidesAfter: 0,
        centeredSlides: false,
        //Queue callbacks
        queueStartCallbacks: false,
        queueEndCallbacks: false,
        //Auto Resize
        autoResize: true,
        resizeReInit: false,
        //DOMAnimation
        DOMAnimation: true,
        //Slides Loader
        loader: {
            slides: [], //array with slides
            slidesHTMLType: 'inner', // or 'outer'
            surroundGroups: 1, //keep preloaded slides groups around view
            logic: 'reload', //or 'change'
            loadAllSlides: false
        },
        //Namespace
        slideElement: 'div',
        slideClass: 'swiper-slide',
        slideActiveClass: 'swiper-slide-active',
        slideVisibleClass: 'swiper-slide-visible',
        slideDuplicateClass: 'swiper-slide-duplicate',
        wrapperClass: 'swiper-wrapper',
        paginationElementClass: 'swiper-pagination-switch',
        paginationActiveClass: 'swiper-active-switch',
        paginationVisibleClass: 'swiper-visible-switch'
    };
    params = params || {};
    for (var prop in defaults) {
        if (prop in params && typeof params[prop] === 'object') {
            for (var subProp in defaults[prop]) {
                if (!(subProp in params[prop])) {
                    params[prop][subProp] = defaults[prop][subProp];
                }
            }
        } else if (!(prop in params)) {
            params[prop] = defaults[prop];
        }
    }
    _this.params = params;
    if (params.scrollContainer) {
        params.freeMode = true;
        params.freeModeFluid = true;
    }
    if (params.loop) {
        params.resistance = '100%';
    }
    var isH = params.mode === 'horizontal';

    /*=========================
     Define Touch Events
     ===========================*/
    var desktopEvents = ['mousedown', 'mousemove', 'mouseup'];
    if (_this.browser.ie10)
        desktopEvents = ['MSPointerDown', 'MSPointerMove', 'MSPointerUp'];
    if (_this.browser.ie11)
        desktopEvents = ['pointerdown', 'pointermove', 'pointerup'];

    _this.touchEvents = {
        touchStart: _this.support.touch || !params.simulateTouch ? 'touchstart' : desktopEvents[0],
        touchMove: _this.support.touch || !params.simulateTouch ? 'touchmove' : desktopEvents[1],
        touchEnd: _this.support.touch || !params.simulateTouch ? 'touchend' : desktopEvents[2]
    };

    /*=========================
     Wrapper
     ===========================*/
    for (var i = _this.container.childNodes.length - 1; i >= 0; i--) {
        if (_this.container.childNodes[i].className) {
            var _wrapperClasses = _this.container.childNodes[i].className.split(/\s+/);
            for (var j = 0; j < _wrapperClasses.length; j++) {
                if (_wrapperClasses[j] === params.wrapperClass) {
                    wrapper = _this.container.childNodes[i];
                }
            }
        }
    }

    _this.wrapper = wrapper;
    /*=========================
     Slide API
     ===========================*/
    _this._extendSwiperSlide = function (el) {
        el.append = function () {
            if (params.loop) {
                el.insertAfter(_this.slides.length - _this.loopedSlides);
            } else {
                _this.wrapper.appendChild(el);
                _this.reInit();
            }

            return el;
        };
        el.prepend = function () {
            if (params.loop) {
                _this.wrapper.insertBefore(el, _this.slides[_this.loopedSlides]);
                _this.removeLoopedSlides();
                _this.calcSlides();
                _this.createLoop();
            } else {
                _this.wrapper.insertBefore(el, _this.wrapper.firstChild);
            }
            _this.reInit();
            return el;
        };
        el.insertAfter = function (index) {
            if (typeof index === 'undefined')
                return false;
            var beforeSlide;

            if (params.loop) {
                beforeSlide = _this.slides[index + 1 + _this.loopedSlides];
                if (beforeSlide) {
                    _this.wrapper.insertBefore(el, beforeSlide);
                } else {
                    _this.wrapper.appendChild(el);
                }
                _this.removeLoopedSlides();
                _this.calcSlides();
                _this.createLoop();
            } else {
                beforeSlide = _this.slides[index + 1];
                _this.wrapper.insertBefore(el, beforeSlide);
            }
            _this.reInit();
            return el;
        };
        el.clone = function () {
            return _this._extendSwiperSlide(el.cloneNode(true));
        };
        el.remove = function () {
            _this.wrapper.removeChild(el);
            _this.reInit();
        };
        el.html = function (html) {
            if (typeof html === 'undefined') {
                return el.innerHTML;
            } else {
                el.innerHTML = html;
                return el;
            }
        };
        el.index = function () {
            var index;
            for (var i = _this.slides.length - 1; i >= 0; i--) {
                if (el === _this.slides[i])
                    index = i;
            }
            return index;
        };
        el.isActive = function () {
            if (el.index() === _this.activeIndex)
                return true;
            else
                return false;
        };
        if (!el.swiperSlideDataStorage)
            el.swiperSlideDataStorage = {};
        el.getData = function (name) {
            return el.swiperSlideDataStorage[name];
        };
        el.setData = function (name, value) {
            el.swiperSlideDataStorage[name] = value;
            return el;
        };
        el.data = function (name, value) {
            if (typeof value === 'undefined') {
                return el.getAttribute('data-' + name);
            } else {
                el.setAttribute('data-' + name, value);
                return el;
            }
        };
        el.getWidth = function (outer, round) {
            return _this.h.getWidth(el, outer, round);
        };
        el.getHeight = function (outer, round) {
            return _this.h.getHeight(el, outer, round);
        };
        el.getOffset = function () {
            return _this.h.getOffset(el);
        };
        return el;
    };

    //Calculate information about number of slides
    _this.calcSlides = function (forceCalcSlides) {
        var oldNumber = _this.slides ? _this.slides.length : false;
        _this.slides = [];
        _this.displaySlides = [];
        for (var i = 0; i < _this.wrapper.childNodes.length; i++) {
            if (_this.wrapper.childNodes[i].className) {
                var _className = _this.wrapper.childNodes[i].className;
                var _slideClasses = _className.split(/\s+/);
                for (var j = 0; j < _slideClasses.length; j++) {
                    if (_slideClasses[j] === params.slideClass) {
                        _this.slides.push(_this.wrapper.childNodes[i]);
                    }
                }
            }
        }
        for (i = _this.slides.length - 1; i >= 0; i--) {
            _this._extendSwiperSlide(_this.slides[i]);
        }
        if (oldNumber === false)
            return;
        if (oldNumber !== _this.slides.length || forceCalcSlides) {

            // Number of slides has been changed
            removeSlideEvents();
            addSlideEvents();
            _this.updateActiveSlide();
            if (_this.params.pagination)
                _this.createPagination();
            _this.callPlugins('numberOfSlidesChanged');
        }
    };

    //Create Slide
    _this.createSlide = function (html, slideClassList, el) {
        slideClassList = slideClassList || _this.params.slideClass;
        el = el || params.slideElement;
        var newSlide = document.createElement(el);
        newSlide.innerHTML = html || '';
        newSlide.className = slideClassList;
        return _this._extendSwiperSlide(newSlide);
    };

    //Append Slide
    _this.appendSlide = function (html, slideClassList, el) {
        if (!html)
            return;
        if (html.nodeType) {
            return _this._extendSwiperSlide(html).append();
        } else {
            return _this.createSlide(html, slideClassList, el).append();
        }
    };
    _this.prependSlide = function (html, slideClassList, el) {
        if (!html)
            return;
        if (html.nodeType) {
            return _this._extendSwiperSlide(html).prepend();
        } else {
            return _this.createSlide(html, slideClassList, el).prepend();
        }
    };
    _this.insertSlideAfter = function (index, html, slideClassList, el) {
        if (typeof index === 'undefined')
            return false;
        if (html.nodeType) {
            return _this._extendSwiperSlide(html).insertAfter(index);
        } else {
            return _this.createSlide(html, slideClassList, el).insertAfter(index);
        }
    };
    _this.removeSlide = function (index) {
        if (_this.slides[index]) {
            if (params.loop) {
                if (!_this.slides[index + _this.loopedSlides])
                    return false;
                _this.slides[index + _this.loopedSlides].remove();
                _this.removeLoopedSlides();
                _this.calcSlides();
                _this.createLoop();
            } else
                _this.slides[index].remove();
            return true;
        } else
            return false;
    };
    _this.removeLastSlide = function () {
        if (_this.slides.length > 0) {
            if (params.loop) {
                _this.slides[_this.slides.length - 1 - _this.loopedSlides].remove();
                _this.removeLoopedSlides();
                _this.calcSlides();
                _this.createLoop();
            } else
                _this.slides[_this.slides.length - 1].remove();
            return true;
        } else {
            return false;
        }
    };
    _this.removeAllSlides = function () {
        for (var i = _this.slides.length - 1; i >= 0; i--) {
            _this.slides[i].remove();
        }
    };
    _this.getSlide = function (index) {
        return _this.slides[index];
    };
    _this.getLastSlide = function () {
        return _this.slides[_this.slides.length - 1];
    };
    _this.getFirstSlide = function () {
        return _this.slides[0];
    };

    //Currently Active Slide
    _this.activeSlide = function () {
        return _this.slides[_this.activeIndex];
    };

    /*=========================
     Wrapper for Callbacks : Allows additive callbacks via function arrays
     ===========================*/
    _this.fireCallback = function () {
        var callback = arguments[0];
        if (Object.prototype.toString.call(callback) === '[object Array]') {
            for (var i = 0; i < callback.length; i++) {
                if (typeof callback[i] === 'function') {
                    callback[i](arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                }
            }
        } else if (Object.prototype.toString.call(callback) === '[object String]') {
            if (params['on' + callback])
                _this.fireCallback(params['on' + callback]);
        } else {
            callback(arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
        }
    };
    function isArray(obj) {
        if (Object.prototype.toString.apply(obj) === '[object Array]')
            return true;
        return false;
    }

    /**
     * Allows user to add callbacks, rather than replace them
     * @param callback
     * @param func
     * @return {*}
     */
    _this.addCallback = function (callback, func) {
        var _this = this, tempFunc;
        if (_this.params['on' + callback]) {
            if (isArray(this.params['on' + callback])) {
                return this.params['on' + callback].push(func);
            } else if (typeof this.params['on' + callback] === 'function') {
                tempFunc = this.params['on' + callback];
                this.params['on' + callback] = [];
                this.params['on' + callback].push(tempFunc);
                return this.params['on' + callback].push(func);
            }
        } else {
            this.params['on' + callback] = [];
            return this.params['on' + callback].push(func);
        }
    };
    _this.removeCallbacks = function (callback) {
        if (_this.params['on' + callback]) {
            _this.params['on' + callback] = null;
        }
    };

    /*=========================
     Plugins API
     ===========================*/
    var _plugins = [];
    for (var plugin in _this.plugins) {
        if (params[plugin]) {
            var p = _this.plugins[plugin](_this, params[plugin]);
            if (p)
                _plugins.push(p);
        }
    }
    _this.callPlugins = function (method, args) {
        if (!args)
            args = {};
        for (var i = 0; i < _plugins.length; i++) {
            if (method in _plugins[i]) {
                _plugins[i][method](args);
            }
        }
    };

    /*=========================
     Windows Phone 8 Fix
     ===========================*/
    if ((_this.browser.ie10 || _this.browser.ie11) && !params.onlyExternal) {
        _this.wrapper.classList.add('swiper-wp8-' + (isH ? 'horizontal' : 'vertical'));
    }

    /*=========================
     Free Mode Class
     ===========================*/
    if (params.freeMode) {
        _this.container.className += ' swiper-free-mode';
    }

    /*==================================================
     Init/Re-init/Resize Fix
     ====================================================*/
    _this.initialized = false;
    _this.init = function (force, forceCalcSlides) {
        var _width = _this.h.getWidth(_this.container, false, params.roundLengths);
        var _height = _this.h.getHeight(_this.container, false, params.roundLengths);
        if (_width === _this.width && _height === _this.height && !force)
            return;

        _this.width = _width;
        _this.height = _height;

        var slideWidth, slideHeight, slideMaxHeight, wrapperWidth, wrapperHeight, slideLeft;
        var i; // loop index variable to avoid JSHint W004 / W038
        containerSize = isH ? _width : _height;
        var wrapper = _this.wrapper;

        if (force) {
            _this.calcSlides(forceCalcSlides);
        }

        if (params.slidesPerView === 'auto') {
            //Auto mode
            var slidesWidth = 0;
            var slidesHeight = 0;

            //Unset Styles
            if (params.slidesOffset > 0) {
                wrapper.style.paddingLeft = '';
                wrapper.style.paddingRight = '';
                wrapper.style.paddingTop = '';
                wrapper.style.paddingBottom = '';
            }
            wrapper.style.width = '';
            wrapper.style.height = '';
            if (params.offsetPxBefore > 0) {
                if (isH)
                    _this.wrapperLeft = params.offsetPxBefore;
                else
                    _this.wrapperTop = params.offsetPxBefore;
            }
            if (params.offsetPxAfter > 0) {
                if (isH)
                    _this.wrapperRight = params.offsetPxAfter;
                else
                    _this.wrapperBottom = params.offsetPxAfter;
            }

            if (params.centeredSlides) {
                if (isH) {
                    _this.wrapperLeft = (containerSize - this.slides[0].getWidth(true, params.roundLengths)) / 2;
                    _this.wrapperRight = (containerSize - _this.slides[_this.slides.length - 1].getWidth(true, params.roundLengths)) / 2;
                } else {
                    _this.wrapperTop = (containerSize - _this.slides[0].getHeight(true, params.roundLengths)) / 2;
                    _this.wrapperBottom = (containerSize - _this.slides[_this.slides.length - 1].getHeight(true, params.roundLengths)) / 2;
                }
            }

            if (isH) {
                if (_this.wrapperLeft >= 0)
                    wrapper.style.paddingLeft = _this.wrapperLeft + 'px';
                if (_this.wrapperRight >= 0)
                    wrapper.style.paddingRight = _this.wrapperRight + 'px';
            } else {
                if (_this.wrapperTop >= 0)
                    wrapper.style.paddingTop = _this.wrapperTop + 'px';
                if (_this.wrapperBottom >= 0)
                    wrapper.style.paddingBottom = _this.wrapperBottom + 'px';
            }
            slideLeft = 0;
            var centeredSlideLeft = 0;
            _this.snapGrid = [];
            _this.slidesGrid = [];

            slideMaxHeight = 0;
            for (i = 0; i < _this.slides.length; i++) {
                slideWidth = _this.slides[i].getWidth(true, params.roundLengths);
                slideHeight = _this.slides[i].getHeight(true, params.roundLengths);
                if (params.calculateHeight) {
                    slideMaxHeight = Math.max(slideMaxHeight, slideHeight);
                }
                var _slideSize = isH ? slideWidth : slideHeight;
                if (params.centeredSlides) {
                    var nextSlideWidth = i === _this.slides.length - 1 ? 0 : _this.slides[i + 1].getWidth(true, params.roundLengths);
                    var nextSlideHeight = i === _this.slides.length - 1 ? 0 : _this.slides[i + 1].getHeight(true, params.roundLengths);
                    var nextSlideSize = isH ? nextSlideWidth : nextSlideHeight;
                    if (_slideSize > containerSize) {
                        if (params.slidesPerViewFit) {
                            _this.snapGrid.push(slideLeft + _this.wrapperLeft);
                            _this.snapGrid.push(slideLeft + _slideSize - containerSize + _this.wrapperLeft);
                        } else {
                            for (var j = 0; j <= Math.floor(_slideSize / (containerSize + _this.wrapperLeft)); j++) {
                                if (j === 0)
                                    _this.snapGrid.push(slideLeft + _this.wrapperLeft);
                                else
                                    _this.snapGrid.push(slideLeft + _this.wrapperLeft + containerSize * j);
                            }
                        }
                        _this.slidesGrid.push(slideLeft + _this.wrapperLeft);
                    } else {
                        _this.snapGrid.push(centeredSlideLeft);
                        _this.slidesGrid.push(centeredSlideLeft);
                    }
                    centeredSlideLeft += _slideSize / 2 + nextSlideSize / 2;
                } else {
                    if (_slideSize > containerSize) {
                        if (params.slidesPerViewFit) {
                            _this.snapGrid.push(slideLeft);
                            _this.snapGrid.push(slideLeft + _slideSize - containerSize);
                        } else {
                            if (containerSize !== 0) {
                                for (var k = 0; k <= Math.floor(_slideSize / containerSize); k++) {
                                    _this.snapGrid.push(slideLeft + containerSize * k);
                                }
                            } else {
                                _this.snapGrid.push(slideLeft);
                            }
                        }

                    } else {
                        _this.snapGrid.push(slideLeft);
                    }
                    _this.slidesGrid.push(slideLeft);
                }

                slideLeft += _slideSize;

                slidesWidth += slideWidth;
                slidesHeight += slideHeight;
            }
            if (params.calculateHeight)
                _this.height = slideMaxHeight;
            if (isH) {
                wrapperSize = slidesWidth + _this.wrapperRight + _this.wrapperLeft;
                wrapper.style.width = (slidesWidth) + 'px';
                wrapper.style.height = (_this.height) + 'px';
            } else {
                wrapperSize = slidesHeight + _this.wrapperTop + _this.wrapperBottom;
                wrapper.style.width = (_this.width) + 'px';
                wrapper.style.height = (slidesHeight) + 'px';
            }

        } else if (params.scrollContainer) {
            //Scroll Container
            wrapper.style.width = '';
            wrapper.style.height = '';
            wrapperWidth = _this.slides[0].getWidth(true, params.roundLengths);
            wrapperHeight = _this.slides[0].getHeight(true, params.roundLengths);
            wrapperSize = isH ? wrapperWidth : wrapperHeight;
            wrapper.style.width = wrapperWidth + 'px';
            wrapper.style.height = wrapperHeight + 'px';
            slideSize = isH ? wrapperWidth : wrapperHeight;

        } else {
            //For usual slides
            if (params.calculateHeight) {
                slideMaxHeight = 0;
                wrapperHeight = 0;
                //ResetWrapperSize
                if (!isH)
                    _this.container.style.height = '';
                wrapper.style.height = '';

                for (i = 0; i < _this.slides.length; i++) {
                    //ResetSlideSize
                    _this.slides[i].style.height = '';
                    slideMaxHeight = Math.max(_this.slides[i].getHeight(true), slideMaxHeight);
                    if (!isH)
                        wrapperHeight += _this.slides[i].getHeight(true);
                }
                slideHeight = slideMaxHeight;
                _this.height = slideHeight;

                if (isH)
                    wrapperHeight = slideHeight;
                else {
                    containerSize = slideHeight;
                    _this.container.style.height = containerSize + 'px';
                }
            } else {
                slideHeight = isH ? _this.height : _this.height / params.slidesPerView;
                if (params.roundLengths)
                    slideHeight = Math.round(slideHeight);
                wrapperHeight = isH ? _this.height : _this.slides.length * slideHeight;
            }
            slideWidth = isH ? _this.width / params.slidesPerView : _this.width;
            if (params.roundLengths)
                slideWidth = Math.round(slideWidth);
            wrapperWidth = isH ? _this.slides.length * slideWidth : _this.width;
            slideSize = isH ? slideWidth : slideHeight;

            if (params.offsetSlidesBefore > 0) {
                if (isH)
                    _this.wrapperLeft = slideSize * params.offsetSlidesBefore;
                else
                    _this.wrapperTop = slideSize * params.offsetSlidesBefore;
            }
            if (params.offsetSlidesAfter > 0) {
                if (isH)
                    _this.wrapperRight = slideSize * params.offsetSlidesAfter;
                else
                    _this.wrapperBottom = slideSize * params.offsetSlidesAfter;
            }
            if (params.offsetPxBefore > 0) {
                if (isH)
                    _this.wrapperLeft = params.offsetPxBefore;
                else
                    _this.wrapperTop = params.offsetPxBefore;
            }
            if (params.offsetPxAfter > 0) {
                if (isH)
                    _this.wrapperRight = params.offsetPxAfter;
                else
                    _this.wrapperBottom = params.offsetPxAfter;
            }
            if (params.centeredSlides) {
                if (isH) {
                    _this.wrapperLeft = (containerSize - slideSize) / 2;
                    _this.wrapperRight = (containerSize - slideSize) / 2;
                } else {
                    _this.wrapperTop = (containerSize - slideSize) / 2;
                    _this.wrapperBottom = (containerSize - slideSize) / 2;
                }
            }
            if (isH) {
                if (_this.wrapperLeft > 0)
                    wrapper.style.paddingLeft = _this.wrapperLeft + 'px';
                if (_this.wrapperRight > 0)
                    wrapper.style.paddingRight = _this.wrapperRight + 'px';
            } else {
                if (_this.wrapperTop > 0)
                    wrapper.style.paddingTop = _this.wrapperTop + 'px';
                if (_this.wrapperBottom > 0)
                    wrapper.style.paddingBottom = _this.wrapperBottom + 'px';
            }

            wrapperSize = isH ? wrapperWidth + _this.wrapperRight + _this.wrapperLeft : wrapperHeight + _this.wrapperTop + _this.wrapperBottom;
            if (!params.cssWidthAndHeight) {
                if (parseFloat(wrapperWidth) > 0) {
                    wrapper.style.width = wrapperWidth + 'px';
                }
                if (parseFloat(wrapperHeight) > 0) {
                    wrapper.style.height = wrapperHeight + 'px';
                }
            }
            slideLeft = 0;
            _this.snapGrid = [];
            _this.slidesGrid = [];
            for (i = 0; i < _this.slides.length; i++) {
                _this.snapGrid.push(slideLeft);
                _this.slidesGrid.push(slideLeft);
                slideLeft += slideSize;
                if (!params.cssWidthAndHeight) {
                    if (parseFloat(slideWidth) > 0) {
                        _this.slides[i].style.width = slideWidth + 'px';
                    }
                    if (parseFloat(slideHeight) > 0) {
                        _this.slides[i].style.height = slideHeight + 'px';
                    }
                }
            }

        }

        if (!_this.initialized) {
            _this.callPlugins('onFirstInit');
            if (params.onFirstInit)
                _this.fireCallback(params.onFirstInit, _this);
        } else {
            _this.callPlugins('onInit');
            if (params.onInit)
                _this.fireCallback(params.onInit, _this);
        }
        _this.initialized = true;
    };

    _this.reInit = function (forceCalcSlides) {
        _this.init(true, forceCalcSlides);
    };

    _this.resizeFix = function (reInit) {
        _this.callPlugins('beforeResizeFix');

        _this.init(params.resizeReInit || reInit);

        // swipe to active slide in fixed mode
        if (!params.freeMode) {
            _this.swipeTo((params.loop ? _this.activeLoopIndex : _this.activeIndex), 0, false);
            // Fix autoplay
            if (params.autoplay) {
                if (_this.support.transitions && typeof autoplayTimeoutId !== 'undefined') {
                    if (typeof autoplayTimeoutId !== 'undefined') {
                        clearTimeout(autoplayTimeoutId);
                        autoplayTimeoutId = undefined;
                        _this.startAutoplay();
                    }
                } else {
                    if (typeof autoplayIntervalId !== 'undefined') {
                        clearInterval(autoplayIntervalId);
                        autoplayIntervalId = undefined;
                        _this.startAutoplay();
                    }
                }
            }
        }
        // move wrapper to the beginning in free mode
        else if (_this.getWrapperTranslate() < -maxWrapperPosition()) {
            _this.setWrapperTransition(0);
            _this.setWrapperTranslate(-maxWrapperPosition());
        }

        _this.callPlugins('afterResizeFix');
    };

    /*==========================================
     Max and Min Positions
     ============================================*/
    function maxWrapperPosition() {
        var a = (wrapperSize - containerSize);
        if (params.freeMode) {
            a = wrapperSize - containerSize;
        }
        // if (params.loop) a -= containerSize;
        if (params.slidesPerView > _this.slides.length && !params.centeredSlides) {
            a = 0;
        }
        if (a < 0)
            a = 0;
        return a;
    }

    /*==========================================
     Event Listeners
     ============================================*/
    function initEvents() {
        var bind = _this.h.addEventListener;
        var eventTarget = params.eventTarget === 'wrapper' ? _this.wrapper : _this.container;
        //Touch Events
        if (!(_this.browser.ie10 || _this.browser.ie11)) {
            if (_this.support.touch) {
                bind(eventTarget, 'touchstart', onTouchStart);
                bind(eventTarget, 'touchmove', onTouchMove);
                bind(eventTarget, 'touchend', onTouchEnd);
            }
            if (params.simulateTouch) {
                bind(eventTarget, 'mousedown', onTouchStart);
                bind(document, 'mousemove', onTouchMove);
                bind(document, 'mouseup', onTouchEnd);
            }
        } else {
            bind(eventTarget, _this.touchEvents.touchStart, onTouchStart);
            bind(document, _this.touchEvents.touchMove, onTouchMove);
            bind(document, _this.touchEvents.touchEnd, onTouchEnd);
        }

        //Resize Event
        if (params.autoResize) {
            bind(window, 'resize', _this.resizeFix);
        }
        //Slide Events
        addSlideEvents();
        //Mousewheel
        _this._wheelEvent = false;
        if (params.mousewheelControl) {
            if (document.onmousewheel !== undefined) {
                _this._wheelEvent = 'mousewheel';
            }
            if (!_this._wheelEvent) {
                try {
                    new WheelEvent('wheel');
                    _this._wheelEvent = 'wheel';
                } catch (e) {
                }
            }
            if (!_this._wheelEvent) {
                _this._wheelEvent = 'DOMMouseScroll';
            }
            if (_this._wheelEvent) {
                bind(_this.container, _this._wheelEvent, handleMousewheel);
            }
        }

        //Keyboard
        function _loadImage(src) {
            var image = new Image();
            image.onload = function () {
                if (_this && _this.imagesLoaded !== undefined)
                    _this.imagesLoaded++;
                if (_this.imagesLoaded === _this.imagesToLoad.length) {
                    _this.reInit();
                    if (params.onImagesReady)
                        _this.fireCallback(params.onImagesReady, _this);
                }
            };
            image.src = src;
        }

        if (params.keyboardControl) {
            bind(document, 'keydown', handleKeyboardKeys);
        }
        if (params.updateOnImagesReady) {
            _this.imagesToLoad = $$('img', _this.container);

            for (var i = 0; i < _this.imagesToLoad.length; i++) {
                _loadImage(_this.imagesToLoad[i].getAttribute('src'));
            }
        }
    }

    //Remove Event Listeners
    _this.destroy = function () {
        var unbind = _this.h.removeEventListener;
        var eventTarget = params.eventTarget === 'wrapper' ? _this.wrapper : _this.container;
        //Touch Events
        if (!(_this.browser.ie10 || _this.browser.ie11)) {
            if (_this.support.touch) {
                unbind(eventTarget, 'touchstart', onTouchStart);
                unbind(eventTarget, 'touchmove', onTouchMove);
                unbind(eventTarget, 'touchend', onTouchEnd);
            }
            if (params.simulateTouch) {
                unbind(eventTarget, 'mousedown', onTouchStart);
                unbind(document, 'mousemove', onTouchMove);
                unbind(document, 'mouseup', onTouchEnd);
            }
        } else {
            unbind(eventTarget, _this.touchEvents.touchStart, onTouchStart);
            unbind(document, _this.touchEvents.touchMove, onTouchMove);
            unbind(document, _this.touchEvents.touchEnd, onTouchEnd);
        }

        //Resize Event
        if (params.autoResize) {
            unbind(window, 'resize', _this.resizeFix);
        }

        //Init Slide Events
        removeSlideEvents();

        //Pagination
        if (params.paginationClickable) {
            removePaginationEvents();
        }

        //Mousewheel
        if (params.mousewheelControl && _this._wheelEvent) {
            unbind(_this.container, _this._wheelEvent, handleMousewheel);
        }

        //Keyboard
        if (params.keyboardControl) {
            unbind(document, 'keydown', handleKeyboardKeys);
        }

        //Stop autoplay
        if (params.autoplay) {
            _this.stopAutoplay();
        }
        _this.callPlugins('onDestroy');

        //Destroy variable
        _this = null;
    };

    function addSlideEvents() {
        var bind = _this.h.addEventListener,
                i;

        //Prevent Links Events
        if (params.preventLinks) {
            var links = $$('a', _this.container);
            for (i = 0; i < links.length; i++) {
                bind(links[i], 'click', preventClick);
            }
        }
        //Release Form Elements
        if (params.releaseFormElements) {
            var formElements = $$('input, textarea, select', _this.container);
            for (i = 0; i < formElements.length; i++) {
                bind(formElements[i], _this.touchEvents.touchStart, releaseForms, true);
            }
        }

        //Slide Clicks & Touches
        if (params.onSlideClick) {
            for (i = 0; i < _this.slides.length; i++) {
                bind(_this.slides[i], 'click', slideClick);
            }
        }
        if (params.onSlideTouch) {
            for (i = 0; i < _this.slides.length; i++) {
                bind(_this.slides[i], _this.touchEvents.touchStart, slideTouch);
            }
        }
    }
    function removeSlideEvents() {
        var unbind = _this.h.removeEventListener,
                i;

        //Slide Clicks & Touches
        if (params.onSlideClick) {
            for (i = 0; i < _this.slides.length; i++) {
                unbind(_this.slides[i], 'click', slideClick);
            }
        }
        if (params.onSlideTouch) {
            for (i = 0; i < _this.slides.length; i++) {
                unbind(_this.slides[i], _this.touchEvents.touchStart, slideTouch);
            }
        }
        //Release Form Elements
        if (params.releaseFormElements) {
            var formElements = $$('input, textarea, select', _this.container);
            for (i = 0; i < formElements.length; i++) {
                unbind(formElements[i], _this.touchEvents.touchStart, releaseForms, true);
            }
        }
        //Prevent Links Events
        if (params.preventLinks) {
            var links = $$('a', _this.container);
            for (i = 0; i < links.length; i++) {
                unbind(links[i], 'click', preventClick);
            }
        }
    }
    /*==========================================
     Keyboard Control
     ============================================*/
    function handleKeyboardKeys(e) {
        var kc = e.keyCode || e.charCode;
        if (e.shiftKey || e.altKey || e.ctrlKey || e.metaKey)
            return;
        if (kc === 37 || kc === 39 || kc === 38 || kc === 40) {
            var inView = false;
            //Check that swiper should be inside of visible area of window
            var swiperOffset = _this.h.getOffset(_this.container);
            var scrollLeft = _this.h.windowScroll().left;
            var scrollTop = _this.h.windowScroll().top;
            var windowWidth = _this.h.windowWidth();
            var windowHeight = _this.h.windowHeight();
            var swiperCoord = [
                [swiperOffset.left, swiperOffset.top],
                [swiperOffset.left + _this.width, swiperOffset.top],
                [swiperOffset.left, swiperOffset.top + _this.height],
                [swiperOffset.left + _this.width, swiperOffset.top + _this.height]
            ];
            for (var i = 0; i < swiperCoord.length; i++) {
                var point = swiperCoord[i];
                if (
                        point[0] >= scrollLeft && point[0] <= scrollLeft + windowWidth &&
                        point[1] >= scrollTop && point[1] <= scrollTop + windowHeight
                        ) {
                    inView = true;
                }

            }
            if (!inView)
                return;
        }
        if (isH) {
            if (kc === 37 || kc === 39) {
                if (e.preventDefault)
                    e.preventDefault();
                else
                    e.returnValue = false;
            }
            if (kc === 39)
                _this.swipeNext();
            if (kc === 37)
                _this.swipePrev();
        } else {
            if (kc === 38 || kc === 40) {
                if (e.preventDefault)
                    e.preventDefault();
                else
                    e.returnValue = false;
            }
            if (kc === 40)
                _this.swipeNext();
            if (kc === 38)
                _this.swipePrev();
        }
    }

    _this.disableKeyboardControl = function () {
        params.keyboardControl = false;
        _this.h.removeEventListener(document, 'keydown', handleKeyboardKeys);
    };

    _this.enableKeyboardControl = function () {
        params.keyboardControl = true;
        _this.h.addEventListener(document, 'keydown', handleKeyboardKeys);
    };

    /*==========================================
     Mousewheel Control
     ============================================*/
    var lastScrollTime = (new Date()).getTime();
    function handleMousewheel(e) {
        var we = _this._wheelEvent;
        var delta = 0;

        //Opera & IE
        if (e.detail)
            delta = -e.detail;
        //WebKits
        else if (we === 'mousewheel') {
            if (params.mousewheelControlForceToAxis) {
                if (isH) {
                    if (Math.abs(e.wheelDeltaX) > Math.abs(e.wheelDeltaY))
                        delta = e.wheelDeltaX;
                    else
                        return;
                } else {
                    if (Math.abs(e.wheelDeltaY) > Math.abs(e.wheelDeltaX))
                        delta = e.wheelDeltaY;
                    else
                        return;
                }
            } else {
                delta = e.wheelDelta;
            }
        }
        //Old FireFox
        else if (we === 'DOMMouseScroll')
            delta = -e.detail;
        //New FireFox
        else if (we === 'wheel') {
            if (params.mousewheelControlForceToAxis) {
                if (isH) {
                    if (Math.abs(e.deltaX) > Math.abs(e.deltaY))
                        delta = -e.deltaX;
                    else
                        return;
                } else {
                    if (Math.abs(e.deltaY) > Math.abs(e.deltaX))
                        delta = -e.deltaY;
                    else
                        return;
                }
            } else {
                delta = Math.abs(e.deltaX) > Math.abs(e.deltaY) ? -e.deltaX : -e.deltaY;
            }
        }

        if (!params.freeMode) {
            if ((new Date()).getTime() - lastScrollTime > 60) {
                if (delta < 0)
                    _this.swipeNext();
                else
                    _this.swipePrev();
            }
            lastScrollTime = (new Date()).getTime();

        } else {
            //Freemode or scrollContainer:
            var position = _this.getWrapperTranslate() + delta;

            if (position > 0)
                position = 0;
            if (position < -maxWrapperPosition())
                position = -maxWrapperPosition();

            _this.setWrapperTransition(0);
            _this.setWrapperTranslate(position);
            _this.updateActiveSlide(position);

            // Return page scroll on edge positions
            if (position === 0 || position === -maxWrapperPosition())
                return;
        }
        if (params.autoplay)
            _this.stopAutoplay(true);

        if (e.preventDefault)
            e.preventDefault();
        else
            e.returnValue = false;
        return false;
    }
    _this.disableMousewheelControl = function () {
        if (!_this._wheelEvent)
            return false;
        params.mousewheelControl = false;
        _this.h.removeEventListener(_this.container, _this._wheelEvent, handleMousewheel);
        return true;
    };

    _this.enableMousewheelControl = function () {
        if (!_this._wheelEvent)
            return false;
        params.mousewheelControl = true;
        _this.h.addEventListener(_this.container, _this._wheelEvent, handleMousewheel);
        return true;
    };

    /*=========================
     Grab Cursor
     ===========================*/
    if (params.grabCursor) {
        var containerStyle = _this.container.style;
        containerStyle.cursor = 'move';
        containerStyle.cursor = 'grab';
        containerStyle.cursor = '-moz-grab';
        containerStyle.cursor = '-webkit-grab';
    }

    /*=========================
     Slides Events Handlers
     ===========================*/

    _this.allowSlideClick = true;
    function slideClick(event) {
        if (_this.allowSlideClick) {
            setClickedSlide(event);
            _this.fireCallback(params.onSlideClick, _this, event);
        }
    }

    function slideTouch(event) {
        setClickedSlide(event);
        _this.fireCallback(params.onSlideTouch, _this, event);
    }

    function setClickedSlide(event) {

        // IE 6-8 support
        if (!event.currentTarget) {
            var element = event.srcElement;
            do {
                if (element.className.indexOf(params.slideClass) > -1) {
                    break;
                }
                element = element.parentNode;
            } while (element);
            _this.clickedSlide = element;
        } else {
            _this.clickedSlide = event.currentTarget;
        }

        _this.clickedSlideIndex = _this.slides.indexOf(_this.clickedSlide);
        _this.clickedSlideLoopIndex = _this.clickedSlideIndex - (_this.loopedSlides || 0);
    }

    _this.allowLinks = true;
    function preventClick(e) {
        if (!_this.allowLinks) {
            if (e.preventDefault)
                e.preventDefault();
            else
                e.returnValue = false;
            if (params.preventLinksPropagation && 'stopPropagation' in e) {
                e.stopPropagation();
            }
            return false;
        }
    }
    function releaseForms(e) {
        if (e.stopPropagation)
            e.stopPropagation();
        else
            e.returnValue = false;
        return false;

    }

    /*==================================================
     Event Handlers
     ====================================================*/
    var isTouchEvent = false;
    var allowThresholdMove;
    var allowMomentumBounce = true;
    function onTouchStart(event) {
        if (params.preventLinks)
            _this.allowLinks = true;
        //Exit if slider is already was touched
        if (_this.isTouched || params.onlyExternal) {
            return false;
        }

        if (params.noSwiping && (event.target || event.srcElement) && noSwipingSlide(event.target || event.srcElement))
            return false;
        allowMomentumBounce = false;
        //Check For Nested Swipers
        _this.isTouched = true;
        isTouchEvent = event.type === 'touchstart';

        if (!isTouchEvent || event.targetTouches.length === 1) {
            _this.callPlugins('onTouchStartBegin');

            if (!isTouchEvent && !_this.isAndroid) {
                if (event.preventDefault)
                    event.preventDefault();
                else
                    event.returnValue = false;
            }

            var pageX = isTouchEvent ? event.targetTouches[0].pageX : (event.pageX || event.clientX);
            var pageY = isTouchEvent ? event.targetTouches[0].pageY : (event.pageY || event.clientY);

            //Start Touches to check the scrolling
            _this.touches.startX = _this.touches.currentX = pageX;
            _this.touches.startY = _this.touches.currentY = pageY;

            _this.touches.start = _this.touches.current = isH ? pageX : pageY;

            //Set Transition Time to 0
            _this.setWrapperTransition(0);

            //Get Start Translate Position
            _this.positions.start = _this.positions.current = _this.getWrapperTranslate();

            //Set Transform
            _this.setWrapperTranslate(_this.positions.start);

            //TouchStartTime
            _this.times.start = (new Date()).getTime();

            //Unset Scrolling
            isScrolling = undefined;

            //Set Treshold
            if (params.moveStartThreshold > 0) {
                allowThresholdMove = false;
            }

            //CallBack
            if (params.onTouchStart)
                _this.fireCallback(params.onTouchStart, _this, event);
            _this.callPlugins('onTouchStartEnd');

        }
    }
    var velocityPrevPosition, velocityPrevTime;
    function onTouchMove(event) {
        // If slider is not touched - exit
        if (!_this.isTouched || params.onlyExternal)
            return;
        if (isTouchEvent && event.type === 'mousemove')
            return;

        var pageX = isTouchEvent ? event.targetTouches[0].pageX : (event.pageX || event.clientX);
        var pageY = isTouchEvent ? event.targetTouches[0].pageY : (event.pageY || event.clientY);

        //check for scrolling
        if (typeof isScrolling === 'undefined' && isH) {
            isScrolling = !!(isScrolling || Math.abs(pageY - _this.touches.startY) > Math.abs(pageX - _this.touches.startX));
        }
        if (typeof isScrolling === 'undefined' && !isH) {
            isScrolling = !!(isScrolling || Math.abs(pageY - _this.touches.startY) < Math.abs(pageX - _this.touches.startX));
        }
        if (isScrolling) {
            _this.isTouched = false;
            return;
        }

        //Check For Nested Swipers
        if (event.assignedToSwiper) {
            _this.isTouched = false;
            return;
        }
        event.assignedToSwiper = true;

        //Block inner links
        if (params.preventLinks) {
            _this.allowLinks = false;
        }
        if (params.onSlideClick) {
            _this.allowSlideClick = false;
        }

        //Stop AutoPlay if exist
        if (params.autoplay) {
            _this.stopAutoplay(true);
        }
        if (!isTouchEvent || event.touches.length === 1) {

            //Moved Flag
            if (!_this.isMoved) {
                _this.callPlugins('onTouchMoveStart');

                if (params.loop) {
                    _this.fixLoop();
                    _this.positions.start = _this.getWrapperTranslate();
                }
                if (params.onTouchMoveStart)
                    _this.fireCallback(params.onTouchMoveStart, _this);
            }
            _this.isMoved = true;

            // cancel event
            if (event.preventDefault)
                event.preventDefault();
            else
                event.returnValue = false;

            _this.touches.current = isH ? pageX : pageY;

            _this.positions.current = (_this.touches.current - _this.touches.start) * params.touchRatio + _this.positions.start;

            //Resistance Callbacks
            if (_this.positions.current > 0 && params.onResistanceBefore) {
                _this.fireCallback(params.onResistanceBefore, _this, _this.positions.current);
            }
            if (_this.positions.current < -maxWrapperPosition() && params.onResistanceAfter) {
                _this.fireCallback(params.onResistanceAfter, _this, Math.abs(_this.positions.current + maxWrapperPosition()));
            }
            //Resistance
            if (params.resistance && params.resistance !== '100%') {
                var resistance;
                //Resistance for Negative-Back sliding
                if (_this.positions.current > 0) {
                    resistance = 1 - _this.positions.current / containerSize / 2;
                    if (resistance < 0.5)
                        _this.positions.current = (containerSize / 2);
                    else
                        _this.positions.current = _this.positions.current * resistance;
                }
                //Resistance for After-End Sliding
                if (_this.positions.current < -maxWrapperPosition()) {

                    var diff = (_this.touches.current - _this.touches.start) * params.touchRatio + (maxWrapperPosition() + _this.positions.start);
                    resistance = (containerSize + diff) / (containerSize);
                    var newPos = _this.positions.current - diff * (1 - resistance) / 2;
                    var stopPos = -maxWrapperPosition() - containerSize / 2;

                    if (newPos < stopPos || resistance <= 0)
                        _this.positions.current = stopPos;
                    else
                        _this.positions.current = newPos;
                }
            }
            if (params.resistance && params.resistance === '100%') {
                //Resistance for Negative-Back sliding
                if (_this.positions.current > 0 && !(params.freeMode && !params.freeModeFluid)) {
                    _this.positions.current = 0;
                }
                //Resistance for After-End Sliding
                if (_this.positions.current < -maxWrapperPosition() && !(params.freeMode && !params.freeModeFluid)) {
                    _this.positions.current = -maxWrapperPosition();
                }
            }
            //Move Slides
            if (!params.followFinger)
                return;

            if (!params.moveStartThreshold) {
                _this.setWrapperTranslate(_this.positions.current);
            } else {
                if (Math.abs(_this.touches.current - _this.touches.start) > params.moveStartThreshold || allowThresholdMove) {
                    if (!allowThresholdMove) {
                        allowThresholdMove = true;
                        _this.touches.start = _this.touches.current;
                        return;
                    }
                    _this.setWrapperTranslate(_this.positions.current);
                } else {
                    _this.positions.current = _this.positions.start;
                }
            }

            if (params.freeMode || params.watchActiveIndex) {
                _this.updateActiveSlide(_this.positions.current);
            }

            //Grab Cursor
            if (params.grabCursor) {
                _this.container.style.cursor = 'move';
                _this.container.style.cursor = 'grabbing';
                _this.container.style.cursor = '-moz-grabbin';
                _this.container.style.cursor = '-webkit-grabbing';
            }
            //Velocity
            if (!velocityPrevPosition)
                velocityPrevPosition = _this.touches.current;
            if (!velocityPrevTime)
                velocityPrevTime = (new Date()).getTime();
            _this.velocity = (_this.touches.current - velocityPrevPosition) / ((new Date()).getTime() - velocityPrevTime) / 2;
            if (Math.abs(_this.touches.current - velocityPrevPosition) < 2)
                _this.velocity = 0;
            velocityPrevPosition = _this.touches.current;
            velocityPrevTime = (new Date()).getTime();
            //Callbacks
            _this.callPlugins('onTouchMoveEnd');
            if (params.onTouchMove)
                _this.fireCallback(params.onTouchMove, _this, event);

            return false;
        }
    }
    function onTouchEnd(event) {
        //Check For scrolling
        if (isScrolling) {
            _this.swipeReset();
        }
        // If slider is not touched exit
        if (params.onlyExternal || !_this.isTouched)
            return;
        _this.isTouched = false;

        //Return Grab Cursor
        if (params.grabCursor) {
            _this.container.style.cursor = 'move';
            _this.container.style.cursor = 'grab';
            _this.container.style.cursor = '-moz-grab';
            _this.container.style.cursor = '-webkit-grab';
        }

        //Check for Current Position
        if (!_this.positions.current && _this.positions.current !== 0) {
            _this.positions.current = _this.positions.start;
        }

        //For case if slider touched but not moved
        if (params.followFinger) {
            _this.setWrapperTranslate(_this.positions.current);
        }

        // TouchEndTime
        _this.times.end = (new Date()).getTime();

        //Difference
        _this.touches.diff = _this.touches.current - _this.touches.start;
        _this.touches.abs = Math.abs(_this.touches.diff);

        _this.positions.diff = _this.positions.current - _this.positions.start;
        _this.positions.abs = Math.abs(_this.positions.diff);

        var diff = _this.positions.diff;
        var diffAbs = _this.positions.abs;
        var timeDiff = _this.times.end - _this.times.start;

        if (diffAbs < 5 && (timeDiff) < 300 && _this.allowLinks === false) {
            if (!params.freeMode && diffAbs !== 0)
                _this.swipeReset();
            //Release inner links
            if (params.preventLinks) {
                _this.allowLinks = true;
            }
            if (params.onSlideClick) {
                _this.allowSlideClick = true;
            }
        }

        setTimeout(function () {
            //Release inner links
            if (params.preventLinks) {
                _this.allowLinks = true;
            }
            if (params.onSlideClick) {
                _this.allowSlideClick = true;
            }
        }, 100);

        var maxPosition = maxWrapperPosition();

        //Not moved or Prevent Negative Back Sliding/After-End Sliding
        if (!_this.isMoved && params.freeMode) {
            _this.isMoved = false;
            if (params.onTouchEnd)
                _this.fireCallback(params.onTouchEnd, _this, event);
            _this.callPlugins('onTouchEnd');
            return;
        }
        if (!_this.isMoved || _this.positions.current > 0 || _this.positions.current < -maxPosition) {
            _this.swipeReset();
            if (params.onTouchEnd)
                _this.fireCallback(params.onTouchEnd, _this, event);
            _this.callPlugins('onTouchEnd');
            return;
        }

        _this.isMoved = false;

        //Free Mode
        if (params.freeMode) {
            if (params.freeModeFluid) {
                var momentumDuration = 1000 * params.momentumRatio;
                var momentumDistance = _this.velocity * momentumDuration;
                var newPosition = _this.positions.current + momentumDistance;
                var doBounce = false;
                var afterBouncePosition;
                var bounceAmount = Math.abs(_this.velocity) * 20 * params.momentumBounceRatio;
                if (newPosition < -maxPosition) {
                    if (params.momentumBounce && _this.support.transitions) {
                        if (newPosition + maxPosition < -bounceAmount)
                            newPosition = -maxPosition - bounceAmount;
                        afterBouncePosition = -maxPosition;
                        doBounce = true;
                        allowMomentumBounce = true;
                    } else
                        newPosition = -maxPosition;
                }
                if (newPosition > 0) {
                    if (params.momentumBounce && _this.support.transitions) {
                        if (newPosition > bounceAmount)
                            newPosition = bounceAmount;
                        afterBouncePosition = 0;
                        doBounce = true;
                        allowMomentumBounce = true;
                    } else
                        newPosition = 0;
                }
                //Fix duration
                if (_this.velocity !== 0)
                    momentumDuration = Math.abs((newPosition - _this.positions.current) / _this.velocity);

                _this.setWrapperTranslate(newPosition);

                _this.setWrapperTransition(momentumDuration);

                if (params.momentumBounce && doBounce) {
                    _this.wrapperTransitionEnd(function () {
                        if (!allowMomentumBounce)
                            return;
                        if (params.onMomentumBounce)
                            _this.fireCallback(params.onMomentumBounce, _this);
                        _this.callPlugins('onMomentumBounce');

                        _this.setWrapperTranslate(afterBouncePosition);
                        _this.setWrapperTransition(300);
                    });
                }

                _this.updateActiveSlide(newPosition);
            }
            if (!params.freeModeFluid || timeDiff >= 300)
                _this.updateActiveSlide(_this.positions.current);

            if (params.onTouchEnd)
                _this.fireCallback(params.onTouchEnd, _this, event);
            _this.callPlugins('onTouchEnd');
            return;
        }

        //Direction
        direction = diff < 0 ? 'toNext' : 'toPrev';

        //Short Touches
        if (direction === 'toNext' && (timeDiff <= 300)) {
            if (diffAbs < 30 || !params.shortSwipes)
                _this.swipeReset();
            else
                _this.swipeNext(true);
        }

        if (direction === 'toPrev' && (timeDiff <= 300)) {
            if (diffAbs < 30 || !params.shortSwipes)
                _this.swipeReset();
            else
                _this.swipePrev(true);
        }

        //Long Touches
        var targetSlideSize = 0;
        if (params.slidesPerView === 'auto') {
            //Define current slide's width
            var currentPosition = Math.abs(_this.getWrapperTranslate());
            var slidesOffset = 0;
            var _slideSize;
            for (var i = 0; i < _this.slides.length; i++) {
                _slideSize = isH ? _this.slides[i].getWidth(true, params.roundLengths) : _this.slides[i].getHeight(true, params.roundLengths);
                slidesOffset += _slideSize;
                if (slidesOffset > currentPosition) {
                    targetSlideSize = _slideSize;
                    break;
                }
            }
            if (targetSlideSize > containerSize)
                targetSlideSize = containerSize;
        } else {
            targetSlideSize = slideSize * params.slidesPerView;
        }
        if (direction === 'toNext' && (timeDiff > 300)) {
            if (diffAbs >= targetSlideSize * params.longSwipesRatio) {
                _this.swipeNext(true);
            } else {
                _this.swipeReset();
            }
        }
        if (direction === 'toPrev' && (timeDiff > 300)) {
            if (diffAbs >= targetSlideSize * params.longSwipesRatio) {
                _this.swipePrev(true);
            } else {
                _this.swipeReset();
            }
        }
        if (params.onTouchEnd)
            _this.fireCallback(params.onTouchEnd, _this, event);
        _this.callPlugins('onTouchEnd');
    }


    /*==================================================
     noSwiping Bubble Check by Isaac Strack
     ====================================================*/
    function noSwipingSlide(el) {
        /*This function is specifically designed to check the parent elements for the noSwiping class, up to the wrapper.
         We need to check parents because while onTouchStart bubbles, _this.isTouched is checked in onTouchStart, which stops the bubbling.
         So, if a text box, for example, is the initial target, and the parent slide container has the noSwiping class, the _this.isTouched
         check will never find it, and what was supposed to be noSwiping is able to be swiped.
         This function will iterate up and check for the noSwiping class in parents, up through the wrapperClass.*/

        // First we create a truthy variable, which is that swiping is allowd (noSwiping = false)
        var noSwiping = false;

        // Now we iterate up (parentElements) until we reach the node with the wrapperClass.
        do {

            // Each time, we check to see if there's a 'swiper-no-swiping' class (noSwipingClass).
            if (el.className.indexOf(params.noSwipingClass) > -1)
            {
                noSwiping = true; // If there is, we set noSwiping = true;
            }

            el = el.parentElement;  // now we iterate up (parent node)

        } while (!noSwiping && el.parentElement && el.className.indexOf(params.wrapperClass) === -1); // also include el.parentElement truthy, just in case.

        // because we didn't check the wrapper itself, we do so now, if noSwiping is false:
        if (!noSwiping && el.className.indexOf(params.wrapperClass) > -1 && el.className.indexOf(params.noSwipingClass) > -1)
            noSwiping = true; // if the wrapper has the noSwipingClass, we set noSwiping = true;

        return noSwiping;
    }

    function addClassToHtmlString(klass, outerHtml) {
        var par = document.createElement('div');
        var child;

        par.innerHTML = outerHtml;
        child = par.firstChild;
        child.className += ' ' + klass;

        return child.outerHTML;
    }


    /*==================================================
     Swipe Functions
     ====================================================*/
    _this.swipeNext = function (internal) {
        if (!internal && params.loop)
            _this.fixLoop();
        if (!internal && params.autoplay)
            _this.stopAutoplay(true);
        _this.callPlugins('onSwipeNext');
        var currentPosition = _this.getWrapperTranslate();
        var newPosition = currentPosition;
        if (params.slidesPerView === 'auto') {
            for (var i = 0; i < _this.snapGrid.length; i++) {
                if (-currentPosition >= _this.snapGrid[i] && -currentPosition < _this.snapGrid[i + 1]) {
                    newPosition = -_this.snapGrid[i + 1];
                    break;
                }
            }
        } else {
            var groupSize = slideSize * params.slidesPerGroup;
            newPosition = -(Math.floor(Math.abs(currentPosition) / Math.floor(groupSize)) * groupSize + groupSize);
        }
        if (newPosition < -maxWrapperPosition()) {
            newPosition = -maxWrapperPosition();
        }
        if (newPosition === currentPosition)
            return false;
        swipeToPosition(newPosition, 'next');
        return true;
    };
    _this.swipePrev = function (internal) {
        if (!internal && params.loop)
            _this.fixLoop();
        if (!internal && params.autoplay)
            _this.stopAutoplay(true);
        _this.callPlugins('onSwipePrev');

        var currentPosition = Math.ceil(_this.getWrapperTranslate());
        var newPosition;
        if (params.slidesPerView === 'auto') {
            newPosition = 0;
            for (var i = 1; i < _this.snapGrid.length; i++) {
                if (-currentPosition === _this.snapGrid[i]) {
                    newPosition = -_this.snapGrid[i - 1];
                    break;
                }
                if (-currentPosition > _this.snapGrid[i] && -currentPosition < _this.snapGrid[i + 1]) {
                    newPosition = -_this.snapGrid[i];
                    break;
                }
            }
        } else {
            var groupSize = slideSize * params.slidesPerGroup;
            newPosition = -(Math.ceil(-currentPosition / groupSize) - 1) * groupSize;
        }

        if (newPosition > 0)
            newPosition = 0;

        if (newPosition === currentPosition)
            return false;
        swipeToPosition(newPosition, 'prev');
        return true;

    };
    _this.swipeReset = function () {
        _this.callPlugins('onSwipeReset');
        var currentPosition = _this.getWrapperTranslate();
        var groupSize = slideSize * params.slidesPerGroup;
        var newPosition;
        var maxPosition = -maxWrapperPosition();
        if (params.slidesPerView === 'auto') {
            newPosition = 0;
            for (var i = 0; i < _this.snapGrid.length; i++) {
                if (-currentPosition === _this.snapGrid[i])
                    return;
                if (-currentPosition >= _this.snapGrid[i] && -currentPosition < _this.snapGrid[i + 1]) {
                    if (_this.positions.diff > 0)
                        newPosition = -_this.snapGrid[i + 1];
                    else
                        newPosition = -_this.snapGrid[i];
                    break;
                }
            }
            if (-currentPosition >= _this.snapGrid[_this.snapGrid.length - 1])
                newPosition = -_this.snapGrid[_this.snapGrid.length - 1];
            if (currentPosition <= -maxWrapperPosition())
                newPosition = -maxWrapperPosition();
        } else {
            newPosition = currentPosition < 0 ? Math.round(currentPosition / groupSize) * groupSize : 0;
        }
        if (params.scrollContainer) {
            newPosition = currentPosition < 0 ? currentPosition : 0;
        }
        if (newPosition < -maxWrapperPosition()) {
            newPosition = -maxWrapperPosition();
        }
        if (params.scrollContainer && (containerSize > slideSize)) {
            newPosition = 0;
        }

        if (newPosition === currentPosition)
            return false;

        swipeToPosition(newPosition, 'reset');
        return true;
    };

    _this.swipeTo = function (index, speed, runCallbacks) {
        index = parseInt(index, 10);
        _this.callPlugins('onSwipeTo', {index: index, speed: speed});
        if (params.loop)
            index = index + _this.loopedSlides;
        var currentPosition = _this.getWrapperTranslate();
        if (index > (_this.slides.length - 1) || index < 0)
            return;
        var newPosition;
        if (params.slidesPerView === 'auto') {
            newPosition = -_this.slidesGrid[index];
        } else {
            newPosition = -index * slideSize;
        }
        if (newPosition < -maxWrapperPosition()) {
            newPosition = -maxWrapperPosition();
        }

        if (newPosition === currentPosition)
            return false;

        runCallbacks = runCallbacks === false ? false : true;
        swipeToPosition(newPosition, 'to', {index: index, speed: speed, runCallbacks: runCallbacks});
        return true;
    };

    function swipeToPosition(newPosition, action, toOptions) {
        var speed = (action === 'to' && toOptions.speed >= 0) ? toOptions.speed : params.speed;
        var timeOld = +new Date();

        function anim() {
            var timeNew = +new Date();
            var time = timeNew - timeOld;
            currentPosition += animationStep * time / (1000 / 60);
            condition = direction === 'toNext' ? currentPosition > newPosition : currentPosition < newPosition;
            if (condition) {
                _this.setWrapperTranslate(Math.round(currentPosition));
                _this._DOMAnimating = true;
                window.setTimeout(function () {
                    anim();
                }, 1000 / 60);
            } else {
                if (params.onSlideChangeEnd) {
                    if (action === 'to') {
                        if (toOptions.runCallbacks === true)
                            _this.fireCallback(params.onSlideChangeEnd, _this);
                    } else {
                        _this.fireCallback(params.onSlideChangeEnd, _this);
                    }

                }
                _this.setWrapperTranslate(newPosition);
                _this._DOMAnimating = false;
            }
        }

        if (_this.support.transitions || !params.DOMAnimation) {
            _this.setWrapperTranslate(newPosition);
            _this.setWrapperTransition(speed);
        } else {
            //Try the DOM animation
            var currentPosition = _this.getWrapperTranslate();
            var animationStep = Math.ceil((newPosition - currentPosition) / speed * (1000 / 60));
            var direction = currentPosition > newPosition ? 'toNext' : 'toPrev';
            var condition = direction === 'toNext' ? currentPosition > newPosition : currentPosition < newPosition;
            if (_this._DOMAnimating)
                return;

            anim();
        }

        //Update Active Slide Index
        _this.updateActiveSlide(newPosition);

        //Callbacks
        if (params.onSlideNext && action === 'next') {
            _this.fireCallback(params.onSlideNext, _this, newPosition);
        }
        if (params.onSlidePrev && action === 'prev') {
            _this.fireCallback(params.onSlidePrev, _this, newPosition);
        }
        //'Reset' Callback
        if (params.onSlideReset && action === 'reset') {
            _this.fireCallback(params.onSlideReset, _this, newPosition);
        }

        //'Next', 'Prev' and 'To' Callbacks
        if (action === 'next' || action === 'prev' || (action === 'to' && toOptions.runCallbacks === true))
            slideChangeCallbacks(action);
    }
    /*==================================================
     Transition Callbacks
     ====================================================*/
    //Prevent Multiple Callbacks
    _this._queueStartCallbacks = false;
    _this._queueEndCallbacks = false;
    function slideChangeCallbacks(direction) {
        //Transition Start Callback
        _this.callPlugins('onSlideChangeStart');
        if (params.onSlideChangeStart) {
            if (params.queueStartCallbacks && _this.support.transitions) {
                if (_this._queueStartCallbacks)
                    return;
                _this._queueStartCallbacks = true;
                _this.fireCallback(params.onSlideChangeStart, _this, direction);
                _this.wrapperTransitionEnd(function () {
                    _this._queueStartCallbacks = false;
                });
            } else
                _this.fireCallback(params.onSlideChangeStart, _this, direction);
        }
        //Transition End Callback
        if (params.onSlideChangeEnd) {
            if (_this.support.transitions) {
                if (params.queueEndCallbacks) {
                    if (_this._queueEndCallbacks)
                        return;
                    _this._queueEndCallbacks = true;
                    _this.wrapperTransitionEnd(function (swiper) {
                        _this.fireCallback(params.onSlideChangeEnd, swiper, direction);
                    });
                } else {
                    _this.wrapperTransitionEnd(function (swiper) {
                        _this.fireCallback(params.onSlideChangeEnd, swiper, direction);
                    });
                }
            } else {
                if (!params.DOMAnimation) {
                    setTimeout(function () {
                        _this.fireCallback(params.onSlideChangeEnd, _this, direction);
                    }, 10);
                }
            }
        }
    }

    /*==================================================
     Update Active Slide Index
     ====================================================*/
    _this.updateActiveSlide = function (position) {
        if (!_this.initialized)
            return;
        if (_this.slides.length === 0)
            return;
        _this.previousIndex = _this.activeIndex;
        if (typeof position === 'undefined')
            position = _this.getWrapperTranslate();
        if (position > 0)
            position = 0;
        var i;
        if (params.slidesPerView === 'auto') {
            var slidesOffset = 0;
            _this.activeIndex = _this.slidesGrid.indexOf(-position);
            if (_this.activeIndex < 0) {
                for (i = 0; i < _this.slidesGrid.length - 1; i++) {
                    if (-position > _this.slidesGrid[i] && -position < _this.slidesGrid[i + 1]) {
                        break;
                    }
                }
                var leftDistance = Math.abs(_this.slidesGrid[i] + position);
                var rightDistance = Math.abs(_this.slidesGrid[i + 1] + position);
                if (leftDistance <= rightDistance)
                    _this.activeIndex = i;
                else
                    _this.activeIndex = i + 1;
            }
        } else {
            _this.activeIndex = Math[params.visibilityFullFit ? 'ceil' : 'round'](-position / slideSize);
        }

        if (_this.activeIndex === _this.slides.length)
            _this.activeIndex = _this.slides.length - 1;
        if (_this.activeIndex < 0)
            _this.activeIndex = 0;

        // Check for slide
        if (!_this.slides[_this.activeIndex])
            return;

        // Calc Visible slides
        _this.calcVisibleSlides(position);

        // Mark visible and active slides with additonal classes
        if (_this.support.classList) {
            var slide;
            for (i = 0; i < _this.slides.length; i++) {
                slide = _this.slides[i];
                slide.classList.remove(params.slideActiveClass);
                if (_this.visibleSlides.indexOf(slide) >= 0) {
                    slide.classList.add(params.slideVisibleClass);
                } else {
                    slide.classList.remove(params.slideVisibleClass);
                }
            }
            _this.slides[_this.activeIndex].classList.add(params.slideActiveClass);
        } else {
            var activeClassRegexp = new RegExp('\\s*' + params.slideActiveClass);
            var inViewClassRegexp = new RegExp('\\s*' + params.slideVisibleClass);

            for (i = 0; i < _this.slides.length; i++) {
                _this.slides[i].className = _this.slides[i].className.replace(activeClassRegexp, '').replace(inViewClassRegexp, '');
                if (_this.visibleSlides.indexOf(_this.slides[i]) >= 0) {
                    _this.slides[i].className += ' ' + params.slideVisibleClass;
                }
            }
            _this.slides[_this.activeIndex].className += ' ' + params.slideActiveClass;
        }

        //Update loop index
        if (params.loop) {
            var ls = _this.loopedSlides;
            _this.activeLoopIndex = _this.activeIndex - ls;
            if (_this.activeLoopIndex >= _this.slides.length - ls * 2) {
                _this.activeLoopIndex = _this.slides.length - ls * 2 - _this.activeLoopIndex;
            }
            if (_this.activeLoopIndex < 0) {
                _this.activeLoopIndex = _this.slides.length - ls * 2 + _this.activeLoopIndex;
            }
            if (_this.activeLoopIndex < 0)
                _this.activeLoopIndex = 0;
        } else {
            _this.activeLoopIndex = _this.activeIndex;
        }
        //Update Pagination
        if (params.pagination) {
            _this.updatePagination(position);
        }
    };
    /*==================================================
     Pagination
     ====================================================*/
    _this.createPagination = function (firstInit) {
        if (params.paginationClickable && _this.paginationButtons) {
            removePaginationEvents();
        }
        _this.paginationContainer = params.pagination.nodeType ? params.pagination : $$(params.pagination)[0];
        if (params.createPagination) {
            var paginationHTML = '';
            var numOfSlides = _this.slides.length;
            var numOfButtons = numOfSlides;
            if (params.loop)
                numOfButtons -= _this.loopedSlides * 2;
            for (var i = 0; i < numOfButtons; i++) {
                paginationHTML += '<' + params.paginationElement + ' class="' + params.paginationElementClass + '"></' + params.paginationElement + '>';
            }
            _this.paginationContainer.innerHTML = paginationHTML;
        }
        _this.paginationButtons = $$('.' + params.paginationElementClass, _this.paginationContainer);
        if (!firstInit)
            _this.updatePagination();
        _this.callPlugins('onCreatePagination');
        if (params.paginationClickable) {
            addPaginationEvents();
        }
    };
    function removePaginationEvents() {
        var pagers = _this.paginationButtons;
        if (pagers) {
            for (var i = 0; i < pagers.length; i++) {
                _this.h.removeEventListener(pagers[i], 'click', paginationClick);
            }
        }
    }
    function addPaginationEvents() {
        var pagers = _this.paginationButtons;
        if (pagers) {
            for (var i = 0; i < pagers.length; i++) {
                _this.h.addEventListener(pagers[i], 'click', paginationClick);
            }
        }
    }
    function paginationClick(e) {
        var index;
        var target = e.target || e.srcElement;
        var pagers = _this.paginationButtons;
        for (var i = 0; i < pagers.length; i++) {
            if (target === pagers[i])
                index = i;
        }
        _this.swipeTo(index);
    }
    _this.updatePagination = function (position) {
        if (!params.pagination)
            return;
        if (_this.slides.length < 1)
            return;
        var activePagers = $$('.' + params.paginationActiveClass, _this.paginationContainer);
        if (!activePagers)
            return;

        //Reset all Buttons' class to not active
        var pagers = _this.paginationButtons;
        if (pagers.length === 0)
            return;
        for (var i = 0; i < pagers.length; i++) {
            pagers[i].className = params.paginationElementClass;
        }

        var indexOffset = params.loop ? _this.loopedSlides : 0;
        if (params.paginationAsRange) {
            if (!_this.visibleSlides)
                _this.calcVisibleSlides(position);
            //Get Visible Indexes
            var visibleIndexes = [];
            var j; // lopp index - avoid JSHint W004 / W038
            for (j = 0; j < _this.visibleSlides.length; j++) {
                var visIndex = _this.slides.indexOf(_this.visibleSlides[j]) - indexOffset;

                if (params.loop && visIndex < 0) {
                    visIndex = _this.slides.length - _this.loopedSlides * 2 + visIndex;
                }
                if (params.loop && visIndex >= _this.slides.length - _this.loopedSlides * 2) {
                    visIndex = _this.slides.length - _this.loopedSlides * 2 - visIndex;
                    visIndex = Math.abs(visIndex);
                }
                visibleIndexes.push(visIndex);
            }

            for (j = 0; j < visibleIndexes.length; j++) {
                if (pagers[visibleIndexes[j]])
                    pagers[visibleIndexes[j]].className += ' ' + params.paginationVisibleClass;
            }

            if (params.loop) {
                if (pagers[_this.activeLoopIndex] !== undefined) {
                    pagers[_this.activeLoopIndex].className += ' ' + params.paginationActiveClass;
                }
            } else {
                pagers[_this.activeIndex].className += ' ' + params.paginationActiveClass;
            }
        } else {
            if (params.loop) {
                if (pagers[_this.activeLoopIndex])
                    pagers[_this.activeLoopIndex].className += ' ' + params.paginationActiveClass + ' ' + params.paginationVisibleClass;
            } else {
                pagers[_this.activeIndex].className += ' ' + params.paginationActiveClass + ' ' + params.paginationVisibleClass;
            }
        }
    };
    _this.calcVisibleSlides = function (position) {
        var visibleSlides = [];
        var _slideLeft = 0, _slideSize = 0, _slideRight = 0;
        if (isH && _this.wrapperLeft > 0)
            position = position + _this.wrapperLeft;
        if (!isH && _this.wrapperTop > 0)
            position = position + _this.wrapperTop;

        for (var i = 0; i < _this.slides.length; i++) {
            _slideLeft += _slideSize;
            if (params.slidesPerView === 'auto')
                _slideSize = isH ? _this.h.getWidth(_this.slides[i], true, params.roundLengths) : _this.h.getHeight(_this.slides[i], true, params.roundLengths);
            else
                _slideSize = slideSize;

            _slideRight = _slideLeft + _slideSize;
            var isVisibile = false;
            if (params.visibilityFullFit) {
                if (_slideLeft >= -position && _slideRight <= -position + containerSize)
                    isVisibile = true;
                if (_slideLeft <= -position && _slideRight >= -position + containerSize)
                    isVisibile = true;
            } else {
                if (_slideRight > -position && _slideRight <= ((-position + containerSize)))
                    isVisibile = true;
                if (_slideLeft >= -position && _slideLeft < ((-position + containerSize)))
                    isVisibile = true;
                if (_slideLeft < -position && _slideRight > ((-position + containerSize)))
                    isVisibile = true;
            }

            if (isVisibile)
                visibleSlides.push(_this.slides[i]);

        }
        if (visibleSlides.length === 0)
            visibleSlides = [_this.slides[_this.activeIndex]];

        _this.visibleSlides = visibleSlides;
    };

    /*==========================================
     Autoplay
     ============================================*/
    var autoplayTimeoutId, autoplayIntervalId;
    _this.startAutoplay = function () {
        if (_this.support.transitions) {
            if (typeof autoplayTimeoutId !== 'undefined')
                return false;
            if (!params.autoplay)
                return;
            _this.callPlugins('onAutoplayStart');
            if (params.onAutoplayStart)
                _this.fireCallback(params.onAutoplayStart, _this);
            autoplay();
        } else {
            if (typeof autoplayIntervalId !== 'undefined')
                return false;
            if (!params.autoplay)
                return;
            _this.callPlugins('onAutoplayStart');
            if (params.onAutoplayStart)
                _this.fireCallback(params.onAutoplayStart, _this);
            autoplayIntervalId = setInterval(function () {
                if (params.loop) {
                    _this.fixLoop();
                    _this.swipeNext(true);
                } else if (!_this.swipeNext(true)) {
                    if (!params.autoplayStopOnLast)
                        _this.swipeTo(0);
                    else {
                        clearInterval(autoplayIntervalId);
                        autoplayIntervalId = undefined;
                    }
                }
            }, params.autoplay);
        }
    };
    _this.stopAutoplay = function (internal) {
        if (_this.support.transitions) {
            if (!autoplayTimeoutId)
                return;
            if (autoplayTimeoutId)
                clearTimeout(autoplayTimeoutId);
            autoplayTimeoutId = undefined;
            if (internal && !params.autoplayDisableOnInteraction) {
                _this.wrapperTransitionEnd(function () {
                    autoplay();
                });
            }
            _this.callPlugins('onAutoplayStop');
            if (params.onAutoplayStop)
                _this.fireCallback(params.onAutoplayStop, _this);
        } else {
            if (autoplayIntervalId)
                clearInterval(autoplayIntervalId);
            autoplayIntervalId = undefined;
            _this.callPlugins('onAutoplayStop');
            if (params.onAutoplayStop)
                _this.fireCallback(params.onAutoplayStop, _this);
        }
    };
    function autoplay() {
        autoplayTimeoutId = setTimeout(function () {
            if (params.loop) {
                _this.fixLoop();
                _this.swipeNext(true);
            } else if (!_this.swipeNext(true)) {
                if (!params.autoplayStopOnLast)
                    _this.swipeTo(0);
                else {
                    clearTimeout(autoplayTimeoutId);
                    autoplayTimeoutId = undefined;
                }
            }
            _this.wrapperTransitionEnd(function () {
                if (typeof autoplayTimeoutId !== 'undefined')
                    autoplay();
            });
        }, params.autoplay);
    }
    /*==================================================
     Loop
     ====================================================*/
    _this.loopCreated = false;
    _this.removeLoopedSlides = function () {
        if (_this.loopCreated) {
            for (var i = 0; i < _this.slides.length; i++) {
                if (_this.slides[i].getData('looped') === true)
                    _this.wrapper.removeChild(_this.slides[i]);
            }
        }
    };

    _this.createLoop = function () {
        if (_this.slides.length === 0)
            return;
        if (params.slidesPerView === 'auto') {
            _this.loopedSlides = params.loopedSlides || 1;
        } else {
            _this.loopedSlides = params.slidesPerView + params.loopAdditionalSlides;
        }

        if (_this.loopedSlides > _this.slides.length) {
            _this.loopedSlides = _this.slides.length;
        }

        var slideFirstHTML = '',
                slideLastHTML = '',
                i;
        var slidesSetFullHTML = '';
        /**
         loopedSlides is too large if loopAdditionalSlides are set.
         Need to divide the slides by maximum number of slides existing.
         
         @author        Tomaz Lovrec <tomaz.lovrec@blanc-noir.at>
         */
        var numSlides = _this.slides.length;
        var fullSlideSets = Math.floor(_this.loopedSlides / numSlides);
        var remainderSlides = _this.loopedSlides % numSlides;
        // assemble full sets of slides
        for (i = 0; i < (fullSlideSets * numSlides); i++) {
            var j = i;
            if (i >= numSlides) {
                var over = Math.floor(i / numSlides);
                j = i - (numSlides * over);
            }
            slidesSetFullHTML += _this.slides[j].outerHTML;
        }
        // assemble remainder slides
        // assemble remainder appended to existing slides
        for (i = 0; i < remainderSlides; i++) {
            slideLastHTML += addClassToHtmlString(params.slideDuplicateClass, _this.slides[i].outerHTML);
        }
        // assemble slides that get preppended to existing slides
        for (i = numSlides - remainderSlides; i < numSlides; i++) {
            slideFirstHTML += addClassToHtmlString(params.slideDuplicateClass, _this.slides[i].outerHTML);
        }
        // assemble all slides
        var slides = slideFirstHTML + slidesSetFullHTML + wrapper.innerHTML + slidesSetFullHTML + slideLastHTML;
        // set the slides
        wrapper.innerHTML = slides;

        _this.loopCreated = true;
        _this.calcSlides();

        //Update Looped Slides with special class
        for (i = 0; i < _this.slides.length; i++) {
            if (i < _this.loopedSlides || i >= _this.slides.length - _this.loopedSlides)
                _this.slides[i].setData('looped', true);
        }
        _this.callPlugins('onCreateLoop');

    };

    _this.fixLoop = function () {
        var newIndex;
        //Fix For Negative Oversliding
        if (_this.activeIndex < _this.loopedSlides) {
            newIndex = _this.slides.length - _this.loopedSlides * 3 + _this.activeIndex;
            _this.swipeTo(newIndex, 0, false);
        }
        //Fix For Positive Oversliding
        else if ((params.slidesPerView === 'auto' && _this.activeIndex >= _this.loopedSlides * 2) || (_this.activeIndex > _this.slides.length - params.slidesPerView * 2)) {
            newIndex = -_this.slides.length + _this.activeIndex + _this.loopedSlides;
            _this.swipeTo(newIndex, 0, false);
        }
    };

    /*==================================================
     Slides Loader
     ====================================================*/
    _this.loadSlides = function () {
        var slidesHTML = '';
        _this.activeLoaderIndex = 0;
        var slides = params.loader.slides;
        var slidesToLoad = params.loader.loadAllSlides ? slides.length : params.slidesPerView * (1 + params.loader.surroundGroups);
        for (var i = 0; i < slidesToLoad; i++) {
            if (params.loader.slidesHTMLType === 'outer')
                slidesHTML += slides[i];
            else {
                slidesHTML += '<' + params.slideElement + ' class="' + params.slideClass + '" data-swiperindex="' + i + '">' + slides[i] + '</' + params.slideElement + '>';
            }
        }
        _this.wrapper.innerHTML = slidesHTML;
        _this.calcSlides(true);
        //Add permanent transitionEnd callback
        if (!params.loader.loadAllSlides) {
            _this.wrapperTransitionEnd(_this.reloadSlides, true);
        }
    };

    _this.reloadSlides = function () {
        var slides = params.loader.slides;
        var newActiveIndex = parseInt(_this.activeSlide().data('swiperindex'), 10);
        if (newActiveIndex < 0 || newActiveIndex > slides.length - 1)
            return; //<-- Exit
        _this.activeLoaderIndex = newActiveIndex;
        var firstIndex = Math.max(0, newActiveIndex - params.slidesPerView * params.loader.surroundGroups);
        var lastIndex = Math.min(newActiveIndex + params.slidesPerView * (1 + params.loader.surroundGroups) - 1, slides.length - 1);
        //Update Transforms
        if (newActiveIndex > 0) {
            var newTransform = -slideSize * (newActiveIndex - firstIndex);
            _this.setWrapperTranslate(newTransform);
            _this.setWrapperTransition(0);
        }
        var i; // loop index
        //New Slides
        if (params.loader.logic === 'reload') {
            _this.wrapper.innerHTML = '';
            var slidesHTML = '';
            for (i = firstIndex; i <= lastIndex; i++) {
                slidesHTML += params.loader.slidesHTMLType === 'outer' ? slides[i] : '<' + params.slideElement + ' class="' + params.slideClass + '" data-swiperindex="' + i + '">' + slides[i] + '</' + params.slideElement + '>';
            }
            _this.wrapper.innerHTML = slidesHTML;
        } else {
            var minExistIndex = 1000;
            var maxExistIndex = 0;

            for (i = 0; i < _this.slides.length; i++) {
                var index = _this.slides[i].data('swiperindex');
                if (index < firstIndex || index > lastIndex) {
                    _this.wrapper.removeChild(_this.slides[i]);
                } else {
                    minExistIndex = Math.min(index, minExistIndex);
                    maxExistIndex = Math.max(index, maxExistIndex);
                }
            }
            for (i = firstIndex; i <= lastIndex; i++) {
                var newSlide;
                if (i < minExistIndex) {
                    newSlide = document.createElement(params.slideElement);
                    newSlide.className = params.slideClass;
                    newSlide.setAttribute('data-swiperindex', i);
                    newSlide.innerHTML = slides[i];
                    _this.wrapper.insertBefore(newSlide, _this.wrapper.firstChild);
                }
                if (i > maxExistIndex) {
                    newSlide = document.createElement(params.slideElement);
                    newSlide.className = params.slideClass;
                    newSlide.setAttribute('data-swiperindex', i);
                    newSlide.innerHTML = slides[i];
                    _this.wrapper.appendChild(newSlide);
                }
            }
        }
        //reInit
        _this.reInit(true);
    };

    /*==================================================
     Make Swiper
     ====================================================*/
    function makeSwiper() {
        _this.calcSlides();
        if (params.loader.slides.length > 0 && _this.slides.length === 0) {
            _this.loadSlides();
        }
        if (params.loop) {
            _this.createLoop();
        }
        _this.init();
        initEvents();
        if (params.pagination) {
            _this.createPagination(true);
        }

        if (params.loop || params.initialSlide > 0) {
            _this.swipeTo(params.initialSlide, 0, false);
        } else {
            _this.updateActiveSlide(0);
        }
        if (params.autoplay) {
            _this.startAutoplay();
        }
        /**
         * Set center slide index.
         *
         * @author        Tomaz Lovrec <tomaz.lovrec@gmail.com>
         */
        _this.centerIndex = _this.activeIndex;

        // Callbacks
        if (params.onSwiperCreated)
            _this.fireCallback(params.onSwiperCreated, _this);
        _this.callPlugins('onSwiperCreated');
    }

    makeSwiper();
};

Swiper.prototype = {
    plugins: {},

    /*==================================================
     Wrapper Operations
     ====================================================*/
    wrapperTransitionEnd: function (callback, permanent) {
        'use strict';
        var a = this,
                el = a.wrapper,
                events = ['webkitTransitionEnd', 'transitionend', 'oTransitionEnd', 'MSTransitionEnd', 'msTransitionEnd'],
                i;

        function fireCallBack() {
            callback(a);
            if (a.params.queueEndCallbacks)
                a._queueEndCallbacks = false;
            if (!permanent) {
                for (i = 0; i < events.length; i++) {
                    a.h.removeEventListener(el, events[i], fireCallBack);
                }
            }
        }

        if (callback) {
            for (i = 0; i < events.length; i++) {
                a.h.addEventListener(el, events[i], fireCallBack);
            }
        }
    },

    getWrapperTranslate: function (axis) {
        'use strict';
        var el = this.wrapper,
                matrix, curTransform, curStyle, transformMatrix;

        // automatic axis detection
        if (typeof axis === 'undefined') {
            axis = this.params.mode === 'horizontal' ? 'x' : 'y';
        }

        if (this.support.transforms && this.params.useCSS3Transforms) {
            curStyle = window.getComputedStyle(el, null);
            if (window.WebKitCSSMatrix) {
                // Some old versions of Webkit choke when 'none' is passed; pass
                // empty string instead in this case
                transformMatrix = new WebKitCSSMatrix(curStyle.webkitTransform === 'none' ? '' : curStyle.webkitTransform);
            } else {
                transformMatrix = curStyle.MozTransform || curStyle.OTransform || curStyle.MsTransform || curStyle.msTransform || curStyle.transform || curStyle.getPropertyValue('transform').replace('translate(', 'matrix(1, 0, 0, 1,');
                matrix = transformMatrix.toString().split(',');
            }

            if (axis === 'x') {
                //Latest Chrome and webkits Fix
                if (window.WebKitCSSMatrix)
                    curTransform = transformMatrix.m41;
                //Crazy IE10 Matrix
                else if (matrix.length === 16)
                    curTransform = parseFloat(matrix[12]);
                //Normal Browsers
                else
                    curTransform = parseFloat(matrix[4]);
            }
            if (axis === 'y') {
                //Latest Chrome and webkits Fix
                if (window.WebKitCSSMatrix)
                    curTransform = transformMatrix.m42;
                //Crazy IE10 Matrix
                else if (matrix.length === 16)
                    curTransform = parseFloat(matrix[13]);
                //Normal Browsers
                else
                    curTransform = parseFloat(matrix[5]);
            }
        } else {
            if (axis === 'x')
                curTransform = parseFloat(el.style.left, 10) || 0;
            if (axis === 'y')
                curTransform = parseFloat(el.style.top, 10) || 0;
        }
        return curTransform || 0;
    },

    setWrapperTranslate: function (x, y, z) {
        'use strict';
        var es = this.wrapper.style,
                coords = {x: 0, y: 0, z: 0},
                translate;

        // passed all coordinates
        if (arguments.length === 3) {
            coords.x = x;
            coords.y = y;
            coords.z = z;
        }

        // passed one coordinate and optional axis
        else {
            if (typeof y === 'undefined') {
                y = this.params.mode === 'horizontal' ? 'x' : 'y';
            }
            coords[y] = x;
        }

        if (this.support.transforms && this.params.useCSS3Transforms) {
            translate = this.support.transforms3d ? 'translate3d(' + coords.x + 'px, ' + coords.y + 'px, ' + coords.z + 'px)' : 'translate(' + coords.x + 'px, ' + coords.y + 'px)';
            es.webkitTransform = es.MsTransform = es.msTransform = es.MozTransform = es.OTransform = es.transform = translate;
        } else {
            es.left = coords.x + 'px';
            es.top = coords.y + 'px';
        }
        this.callPlugins('onSetWrapperTransform', coords);
        if (this.params.onSetWrapperTransform)
            this.fireCallback(this.params.onSetWrapperTransform, this, coords);
    },

    setWrapperTransition: function (duration) {
        'use strict';
        var es = this.wrapper.style;
        es.webkitTransitionDuration = es.MsTransitionDuration = es.msTransitionDuration = es.MozTransitionDuration = es.OTransitionDuration = es.transitionDuration = (duration / 1000) + 's';
        this.callPlugins('onSetWrapperTransition', {duration: duration});
        if (this.params.onSetWrapperTransition)
            this.fireCallback(this.params.onSetWrapperTransition, this, duration);

    },

    /*==================================================
     Helpers
     ====================================================*/
    h: {
        getWidth: function (el, outer, round) {
            'use strict';
            var width = window.getComputedStyle(el, null).getPropertyValue('width');
            var returnWidth = parseFloat(width);
            //IE Fixes
            if (isNaN(returnWidth) || width.indexOf('%') > 0) {
                returnWidth = el.offsetWidth - parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-left')) - parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-right'));
            }
            if (outer)
                returnWidth += parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-left')) + parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-right'));
            if (round)
                return Math.round(returnWidth);
            else
                return returnWidth;
        },
        getHeight: function (el, outer, round) {
            'use strict';
            if (outer)
                return el.offsetHeight;

            var height = window.getComputedStyle(el, null).getPropertyValue('height');
            var returnHeight = parseFloat(height);
            //IE Fixes
            if (isNaN(returnHeight) || height.indexOf('%') > 0) {
                returnHeight = el.offsetHeight - parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-top')) - parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-bottom'));
            }
            if (outer)
                returnHeight += parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-top')) + parseFloat(window.getComputedStyle(el, null).getPropertyValue('padding-bottom'));
            if (round)
                return Math.round(returnHeight);
            else
                return returnHeight;
        },
        getOffset: function (el) {
            'use strict';
            var box = el.getBoundingClientRect();
            var body = document.body;
            var clientTop = el.clientTop || body.clientTop || 0;
            var clientLeft = el.clientLeft || body.clientLeft || 0;
            var scrollTop = window.pageYOffset || el.scrollTop;
            var scrollLeft = window.pageXOffset || el.scrollLeft;
            if (document.documentElement && !window.pageYOffset) {
                //IE7-8
                scrollTop = document.documentElement.scrollTop;
                scrollLeft = document.documentElement.scrollLeft;
            }
            return {
                top: box.top + scrollTop - clientTop,
                left: box.left + scrollLeft - clientLeft
            };
        },
        windowWidth: function () {
            'use strict';
            if (window.innerWidth)
                return window.innerWidth;
            else if (document.documentElement && document.documentElement.clientWidth)
                return document.documentElement.clientWidth;
        },
        windowHeight: function () {
            'use strict';
            if (window.innerHeight)
                return window.innerHeight;
            else if (document.documentElement && document.documentElement.clientHeight)
                return document.documentElement.clientHeight;
        },
        windowScroll: function () {
            'use strict';
            if (typeof pageYOffset !== 'undefined') {
                return {
                    left: window.pageXOffset,
                    top: window.pageYOffset
                };
            } else if (document.documentElement) {
                return {
                    left: document.documentElement.scrollLeft,
                    top: document.documentElement.scrollTop
                };
            }
        },

        addEventListener: function (el, event, listener, useCapture) {
            'use strict';
            if (typeof useCapture === 'undefined') {
                useCapture = false;
            }

            if (el.addEventListener) {
                el.addEventListener(event, listener, useCapture);
            } else if (el.attachEvent) {
                el.attachEvent('on' + event, listener);
            }
        },

        removeEventListener: function (el, event, listener, useCapture) {
            'use strict';
            if (typeof useCapture === 'undefined') {
                useCapture = false;
            }

            if (el.removeEventListener) {
                el.removeEventListener(event, listener, useCapture);
            } else if (el.detachEvent) {
                el.detachEvent('on' + event, listener);
            }
        }
    },
    setTransform: function (el, transform) {
        'use strict';
        var es = el.style;
        es.webkitTransform = es.MsTransform = es.msTransform = es.MozTransform = es.OTransform = es.transform = transform;
    },
    setTranslate: function (el, translate) {
        'use strict';
        var es = el.style;
        var pos = {
            x: translate.x || 0,
            y: translate.y || 0,
            z: translate.z || 0
        };
        var transformString = this.support.transforms3d ? 'translate3d(' + (pos.x) + 'px,' + (pos.y) + 'px,' + (pos.z) + 'px)' : 'translate(' + (pos.x) + 'px,' + (pos.y) + 'px)';
        es.webkitTransform = es.MsTransform = es.msTransform = es.MozTransform = es.OTransform = es.transform = transformString;
        if (!this.support.transforms) {
            es.left = pos.x + 'px';
            es.top = pos.y + 'px';
        }
    },
    setTransition: function (el, duration) {
        'use strict';
        var es = el.style;
        es.webkitTransitionDuration = es.MsTransitionDuration = es.msTransitionDuration = es.MozTransitionDuration = es.OTransitionDuration = es.transitionDuration = duration + 'ms';
    },
    /*==================================================
     Feature Detection
     ====================================================*/
    support: {

        touch: (window.Modernizr && Modernizr.touch === true) || (function () {
            'use strict';
            return !!(('ontouchstart' in window) || window.DocumentTouch && document instanceof DocumentTouch);
        })(),

        transforms3d: (window.Modernizr && Modernizr.csstransforms3d === true) || (function () {
            'use strict';
            var div = document.createElement('div').style;
            return ('webkitPerspective' in div || 'MozPerspective' in div || 'OPerspective' in div || 'MsPerspective' in div || 'perspective' in div);
        })(),

        transforms: (window.Modernizr && Modernizr.csstransforms === true) || (function () {
            'use strict';
            var div = document.createElement('div').style;
            return ('transform' in div || 'WebkitTransform' in div || 'MozTransform' in div || 'msTransform' in div || 'MsTransform' in div || 'OTransform' in div);
        })(),

        transitions: (window.Modernizr && Modernizr.csstransitions === true) || (function () {
            'use strict';
            var div = document.createElement('div').style;
            return ('transition' in div || 'WebkitTransition' in div || 'MozTransition' in div || 'msTransition' in div || 'MsTransition' in div || 'OTransition' in div);
        })(),

        classList: (function () {
            'use strict';
            var div = document.createElement('div').style;
            return 'classList' in div;
        })()
    },

    browser: {

        ie8: (function () {
            'use strict';
            var rv = -1; // Return value assumes failure.
            if (navigator.appName === 'Microsoft Internet Explorer') {
                var ua = navigator.userAgent;
                var re = new RegExp(/MSIE ([0-9]{1,}[\.0-9]{0,})/);
                if (re.exec(ua) !== null)
                    rv = parseFloat(RegExp.$1);
            }
            return rv !== -1 && rv < 9;
        })(),

        ie10: window.navigator.msPointerEnabled,
        ie11: window.navigator.pointerEnabled
    }
};

/*=========================
 jQuery & Zepto Plugins
 ===========================*/
if (window.jQuery || window.Zepto) {
    (function ($) {
        'use strict';
        $.fn.swiper = function (params) {
            var s = new Swiper($(this)[0], params);
            $(this).data('swiper', s);
            return s;
        };
    })(window.jQuery || window.Zepto);
}

// component
if (typeof (module) !== 'undefined')
{
    module.exports = Swiper;
}

// requirejs support
if (typeof define === 'function' && define.amd) {
    define([], function () {
        'use strict';
        return Swiper;
    });
}





// Generated by CoffeeScript 1.6.1
(function () {

    (function ($, window, document) {
        var Plugin, defaults, pluginName;
        pluginName = "slidesjs";
        defaults = {
            width: 940,
            height: 528,
            start: 1,
            navigation: {
                active: true,
                effect: "slide"
            },
            pagination: {
                active: true,
                effect: "slide"
            },
            play: {
                active: false,
                effect: "slide",
                interval: 5000,
                auto: false,
                swap: true,
                pauseOnHover: false,
                restartDelay: 2500
            },
            effect: {
                slide: {
                    speed: 500
                },
                fade: {
                    speed: 300,
                    crossfade: true
                }
            },
            callback: {
                loaded: function () {},
                start: function () {},
                complete: function () {}
            }
        };
        Plugin = (function () {

            function Plugin(element, options) {
                this.element = element;
                this.options = $.extend(true, {}, defaults, options);
                this._defaults = defaults;
                this._name = pluginName;
                this.init();
            }

            return Plugin;

        })();
        Plugin.prototype.init = function () {
            var $element, nextButton, pagination, playButton, prevButton, stopButton,
                    _this = this;
            $element = $(this.element);
            this.data = $.data(this);
            $.data(this, "animating", false);
            $.data(this, "total", $element.children().not(".slidesjs-navigation", $element).length);
            $.data(this, "current", this.options.start - 1);
            $.data(this, "vendorPrefix", this._getVendorPrefix());
            if (typeof TouchEvent !== "undefined") {
                $.data(this, "touch", true);
                this.options.effect.slide.speed = this.options.effect.slide.speed / 2;
            }
            $element.css({
                overflow: "hidden"
            });
            $element.slidesContainer = $element.children().not(".slidesjs-navigation", $element).wrapAll("<div class='slidesjs-container'>", $element).parent().css({
                overflow: "hidden",
                position: "relative"
            });
            $(".slidesjs-container", $element).wrapInner("<div class='slidesjs-control'>", $element).children();
            $(".slidesjs-control", $element).css({
                position: "relative",
                left: 0
            });
            $(".slidesjs-control", $element).children().addClass("slidesjs-slide").css({
                position: "absolute",
                top: 0,
                left: 0,
                width: "100%",
                zIndex: 0,
                display: "none",
                webkitBackfaceVisibility: "hidden"
            });
            $.each($(".slidesjs-control", $element).children(), function (i) {
                var $slide;
                $slide = $(this);
                return $slide.attr("slidesjs-index", i);
            });
            if (this.data.touch) {
                $(".slidesjs-control", $element).on("touchstart", function (e) {
                    return _this._touchstart(e);
                });
                $(".slidesjs-control", $element).on("touchmove", function (e) {
                    return _this._touchmove(e);
                });
                $(".slidesjs-control", $element).on("touchend", function (e) {
                    return _this._touchend(e);
                });
            }
            $element.fadeIn(0);
            this.update();
            if (this.data.touch) {
                this._setuptouch();
            }
            $(".slidesjs-control", $element).children(":eq(" + this.data.current + ")").eq(0).fadeIn(0, function () {
                return $(this).css({
                    zIndex: 10
                });
            });
            if (this.options.navigation.active) {
                prevButton = $("<a>", {
                    "class": "slidesjs-previous slidesjs-navigation",
                    href: "#",
                    title: "Previous",
                    text: "Previous"
                }).appendTo($element);
                nextButton = $("<a>", {
                    "class": "slidesjs-next slidesjs-navigation",
                    href: "#",
                    title: "Next",
                    text: "Next"
                }).appendTo($element);
            }
            $(".slidesjs-next", $element).click(function (e) {
                e.preventDefault();
                _this.stop(true);
                return _this.next(_this.options.navigation.effect);
            });
            $(".slidesjs-previous", $element).click(function (e) {
                e.preventDefault();
                _this.stop(true);
                return _this.previous(_this.options.navigation.effect);
            });
            if (this.options.play.active) {
                playButton = $("<a>", {
                    "class": "slidesjs-play slidesjs-navigation",
                    href: "#",
                    title: "Play",
                    text: "Play"
                }).appendTo($element);
                stopButton = $("<a>", {
                    "class": "slidesjs-stop slidesjs-navigation",
                    href: "#",
                    title: "Stop",
                    text: "Stop"
                }).appendTo($element);
                playButton.click(function (e) {
                    e.preventDefault();
                    return _this.play(true);
                });
                stopButton.click(function (e) {
                    e.preventDefault();
                    return _this.stop(true);
                });
                if (this.options.play.swap) {
                    stopButton.css({
                        display: "none"
                    });
                }
            }
            if (this.options.pagination.active) {
                pagination = $("<ul>", {
                    "class": "slidesjs-pagination"
                }).appendTo($element);
                $.each(new Array(this.data.total), function (i) {
                    var paginationItem, paginationLink;
                    paginationItem = $("<li>", {
                        "class": "slidesjs-pagination-item"
                    }).appendTo(pagination);
                    paginationLink = $("<a>", {
                        href: "#",
                        "data-slidesjs-item": i,
                        html: i + 1
                    }).appendTo(paginationItem);
                    return paginationLink.click(function (e) {
                        e.preventDefault();
                        _this.stop(true);
                        return _this.goto(($(e.currentTarget).attr("data-slidesjs-item") * 1) + 1);
                    });
                });
            }
            $(window).bind("resize", function () {
                return _this.update();
            });
            this._setActive();
            if (this.options.play.auto) {
                this.play();
            }
            return this.options.callback.loaded(this.options.start);
        };
        Plugin.prototype._setActive = function (number) {
            var $element, current;
            $element = $(this.element);
            this.data = $.data(this);
            current = number > -1 ? number : this.data.current;
            $(".active", $element).removeClass("active");
            return $(".slidesjs-pagination li:eq(" + current + ") a", $element).addClass("active");
        };
        Plugin.prototype.update = function () {
            var $element, height, width;
            $element = $(this.element);
            this.data = $.data(this);
            $(".slidesjs-control", $element).children(":not(:eq(" + this.data.current + "))").css({
                display: "none",
                left: 0,
                zIndex: 0
            });
            width = $element.width();
            height = (this.options.height / this.options.width) * width;
            this.options.width = width;
            this.options.height = height;
            return $(".slidesjs-control, .slidesjs-container", $element).css({
                width: width,
                height: height
            });
        };
        Plugin.prototype.next = function (effect) {
            var $element;
            $element = $(this.element);
            this.data = $.data(this);
            $.data(this, "direction", "next");
            if (effect === void 0) {
                effect = this.options.navigation.effect;
            }
            if (effect === "fade") {
                return this._fade();
            } else {
                return this._slide();
            }
        };
        Plugin.prototype.previous = function (effect) {
            var $element;
            $element = $(this.element);
            this.data = $.data(this);
            $.data(this, "direction", "previous");
            if (effect === void 0) {
                effect = this.options.navigation.effect;
            }
            if (effect === "fade") {
                return this._fade();
            } else {
                return this._slide();
            }
        };
        Plugin.prototype.goto = function (number) {
            var $element, effect;
            $element = $(this.element);
            this.data = $.data(this);
            if (effect === void 0) {
                effect = this.options.pagination.effect;
            }
            if (number > this.data.total) {
                number = this.data.total;
            } else if (number < 1) {
                number = 1;
            }
            if (typeof number === "number") {
                if (effect === "fade") {
                    return this._fade(number);
                } else {
                    return this._slide(number);
                }
            } else if (typeof number === "string") {
                if (number === "first") {
                    if (effect === "fade") {
                        return this._fade(0);
                    } else {
                        return this._slide(0);
                    }
                } else if (number === "last") {
                    if (effect === "fade") {
                        return this._fade(this.data.total);
                    } else {
                        return this._slide(this.data.total);
                    }
                }
            }
        };
        Plugin.prototype._setuptouch = function () {
            var $element, next, previous, slidesControl;
            $element = $(this.element);
            this.data = $.data(this);
            slidesControl = $(".slidesjs-control", $element);
            next = this.data.current + 1;
            previous = this.data.current - 1;
            if (previous < 0) {
                previous = this.data.total - 1;
            }
            if (next > this.data.total - 1) {
                next = 0;
            }
            slidesControl.children(":eq(" + next + ")").css({
                display: "block",
                left: this.options.width
            });
            return slidesControl.children(":eq(" + previous + ")").css({
                display: "block",
                left: -this.options.width
            });
        };
        Plugin.prototype._touchstart = function (e) {
            var $element, touches;
            $element = $(this.element);
            this.data = $.data(this);
            touches = e.originalEvent.touches[0];
            this._setuptouch();
            $.data(this, "touchtimer", Number(new Date()));
            $.data(this, "touchstartx", touches.pageX);
            $.data(this, "touchstarty", touches.pageY);
            return e.stopPropagation();
        };
        Plugin.prototype._touchend = function (e) {
            var $element, duration, prefix, slidesControl, timing, touches, transform,
                    _this = this;
            $element = $(this.element);
            this.data = $.data(this);
            touches = e.originalEvent.touches[0];
            slidesControl = $(".slidesjs-control", $element);
            if (slidesControl.position().left > this.options.width * 0.5 || slidesControl.position().left > this.options.width * 0.1 && (Number(new Date()) - this.data.touchtimer < 250)) {
                $.data(this, "direction", "previous");
                this._slide();
            } else if (slidesControl.position().left < -(this.options.width * 0.5) || slidesControl.position().left < -(this.options.width * 0.1) && (Number(new Date()) - this.data.touchtimer < 250)) {
                $.data(this, "direction", "next");
                this._slide();
            } else {
                prefix = this.data.vendorPrefix;
                transform = prefix + "Transform";
                duration = prefix + "TransitionDuration";
                timing = prefix + "TransitionTimingFunction";
                slidesControl[0].style[transform] = "translateX(0px)";
                slidesControl[0].style[duration] = this.options.effect.slide.speed * 0.85 + "ms";
            }
            slidesControl.on("transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd", function () {
                prefix = _this.data.vendorPrefix;
                transform = prefix + "Transform";
                duration = prefix + "TransitionDuration";
                timing = prefix + "TransitionTimingFunction";
                slidesControl[0].style[transform] = "";
                slidesControl[0].style[duration] = "";
                return slidesControl[0].style[timing] = "";
            });
            return e.stopPropagation();
        };
        Plugin.prototype._touchmove = function (e) {
            var $element, prefix, slidesControl, touches, transform;
            $element = $(this.element);
            this.data = $.data(this);
            touches = e.originalEvent.touches[0];
            prefix = this.data.vendorPrefix;
            slidesControl = $(".slidesjs-control", $element);
            transform = prefix + "Transform";
            $.data(this, "scrolling", Math.abs(touches.pageX - this.data.touchstartx) < Math.abs(touches.pageY - this.data.touchstarty));
            if (!this.data.animating && !this.data.scrolling) {
                e.preventDefault();
                this._setuptouch();
                slidesControl[0].style[transform] = "translateX(" + (touches.pageX - this.data.touchstartx) + "px)";
            }
            return e.stopPropagation();
        };
        Plugin.prototype.play = function (next) {
            var $element, currentSlide, slidesContainer,
                    _this = this;
            $element = $(this.element);
            this.data = $.data(this);
            if (!this.data.playInterval) {
                if (next) {
                    currentSlide = this.data.current;
                    this.data.direction = "next";
                    if (this.options.play.effect === "fade") {
                        this._fade();
                    } else {
                        this._slide();
                    }
                }
                $.data(this, "playInterval", setInterval((function () {
                    currentSlide = _this.data.current;
                    _this.data.direction = "next";
                    if (_this.options.play.effect === "fade") {
                        return _this._fade();
                    } else {
                        return _this._slide();
                    }
                }), this.options.play.interval));
                slidesContainer = $(".slidesjs-container", $element);
                if (this.options.play.pauseOnHover) {
                    slidesContainer.unbind();
                    slidesContainer.bind("mouseenter", function () {
                        return _this.stop();
                    });
                    slidesContainer.bind("mouseleave", function () {
                        if (_this.options.play.restartDelay) {
                            return $.data(_this, "restartDelay", setTimeout((function () {
                                return _this.play(true);
                            }), _this.options.play.restartDelay));
                        } else {
                            return _this.play();
                        }
                    });
                }
                $.data(this, "playing", true);
                $(".slidesjs-play", $element).addClass("slidesjs-playing");
                if (this.options.play.swap) {
                    $(".slidesjs-play", $element).hide();
                    return $(".slidesjs-stop", $element).show();
                }
            }
        };
        Plugin.prototype.stop = function (clicked) {
            var $element;
            $element = $(this.element);
            this.data = $.data(this);
            clearInterval(this.data.playInterval);
            if (this.options.play.pauseOnHover && clicked) {
                $(".slidesjs-container", $element).unbind();
            }
            $.data(this, "playInterval", null);
            $.data(this, "playing", false);
            $(".slidesjs-play", $element).removeClass("slidesjs-playing");
            if (this.options.play.swap) {
                $(".slidesjs-stop", $element).hide();
                return $(".slidesjs-play", $element).show();
            }
        };
        Plugin.prototype._slide = function (number) {
            var $element, currentSlide, direction, duration, next, prefix, slidesControl, timing, transform, value,
                    _this = this;
            $element = $(this.element);
            this.data = $.data(this);
            if (!this.data.animating && number !== this.data.current + 1) {
                $.data(this, "animating", true);
                currentSlide = this.data.current;
                if (number > -1) {
                    number = number - 1;
                    value = number > currentSlide ? 1 : -1;
                    direction = number > currentSlide ? -this.options.width : this.options.width;
                    next = number;
                } else {
                    value = this.data.direction === "next" ? 1 : -1;
                    direction = this.data.direction === "next" ? -this.options.width : this.options.width;
                    next = currentSlide + value;
                }
                if (next === -1) {
                    next = this.data.total - 1;
                }
                if (next === this.data.total) {
                    next = 0;
                }
                this._setActive(next);
                slidesControl = $(".slidesjs-control", $element);
                if (number > -1) {
                    slidesControl.children(":not(:eq(" + currentSlide + "))").css({
                        display: "none",
                        left: 0,
                        zIndex: 0
                    });
                }
                slidesControl.children(":eq(" + next + ")").css({
                    display: "block",
                    left: value * this.options.width,
                    zIndex: 10
                });
                this.options.callback.start(currentSlide + 1);
                if (this.data.vendorPrefix) {
                    prefix = this.data.vendorPrefix;
                    transform = prefix + "Transform";
                    duration = prefix + "TransitionDuration";
                    timing = prefix + "TransitionTimingFunction";
                    slidesControl[0].style[transform] = "translateX(" + direction + "px)";
                    slidesControl[0].style[duration] = this.options.effect.slide.speed + "ms";
                    return slidesControl.on("transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd", function () {
                        slidesControl[0].style[transform] = "";
                        slidesControl[0].style[duration] = "";
                        slidesControl.children(":eq(" + next + ")").css({
                            left: 0
                        });
                        slidesControl.children(":eq(" + currentSlide + ")").css({
                            display: "none",
                            left: 0,
                            zIndex: 0
                        });
                        $.data(_this, "current", next);
                        $.data(_this, "animating", false);
                        slidesControl.unbind("transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd");
                        slidesControl.children(":not(:eq(" + next + "))").css({
                            display: "none",
                            left: 0,
                            zIndex: 0
                        });
                        if (_this.data.touch) {
                            _this._setuptouch();
                        }
                        return _this.options.callback.complete(next + 1);
                    });
                } else {
                    return slidesControl.stop().animate({
                        left: direction
                    }, this.options.effect.slide.speed, (function () {
                        slidesControl.css({
                            left: 0
                        });
                        slidesControl.children(":eq(" + next + ")").css({
                            left: 0
                        });
                        return slidesControl.children(":eq(" + currentSlide + ")").css({
                            display: "none",
                            left: 0,
                            zIndex: 0
                        }, $.data(_this, "current", next), $.data(_this, "animating", false), _this.options.callback.complete(next + 1));
                    }));
                }
            }
        };
        Plugin.prototype._fade = function (number) {
            var $element, currentSlide, next, slidesControl, value,
                    _this = this;
            $element = $(this.element);
            this.data = $.data(this);
            if (!this.data.animating && number !== this.data.current + 1) {
                $.data(this, "animating", true);
                currentSlide = this.data.current;
                if (number) {
                    number = number - 1;
                    value = number > currentSlide ? 1 : -1;
                    next = number;
                } else {
                    value = this.data.direction === "next" ? 1 : -1;
                    next = currentSlide + value;
                }
                if (next === -1) {
                    next = this.data.total - 1;
                }
                if (next === this.data.total) {
                    next = 0;
                }
                this._setActive(next);
                slidesControl = $(".slidesjs-control", $element);
                slidesControl.children(":eq(" + next + ")").css({
                    display: "none",
                    left: 0,
                    zIndex: 10
                });
                this.options.callback.start(currentSlide + 1);
                if (this.options.effect.fade.crossfade) {
                    slidesControl.children(":eq(" + this.data.current + ")").stop().fadeOut(this.options.effect.fade.speed);
                    return slidesControl.children(":eq(" + next + ")").stop().fadeIn(this.options.effect.fade.speed, (function () {
                        slidesControl.children(":eq(" + next + ")").css({
                            zIndex: 0
                        });
                        $.data(_this, "animating", false);
                        $.data(_this, "current", next);
                        return _this.options.callback.complete(next + 1);
                    }));
                } else {
                    return slidesControl.children(":eq(" + currentSlide + ")").stop().fadeOut(this.options.effect.fade.speed, (function () {
                        slidesControl.children(":eq(" + next + ")").stop().fadeIn(_this.options.effect.fade.speed, (function () {
                            return slidesControl.children(":eq(" + next + ")").css({
                                zIndex: 10
                            });
                        }));
                        $.data(_this, "animating", false);
                        $.data(_this, "current", next);
                        return _this.options.callback.complete(next + 1);
                    }));
                }
            }
        };
        Plugin.prototype._getVendorPrefix = function () {
            var body, i, style, transition, vendor;
            body = document.body || document.documentElement;
            style = body.style;
            transition = "transition";
            vendor = ["Moz", "Webkit", "Khtml", "O", "ms"];
            transition = transition.charAt(0).toUpperCase() + transition.substr(1);
            i = 0;
            while (i < vendor.length) {
                if (typeof style[vendor[i] + transition] === "string") {
                    return vendor[i];
                }
                i++;
            }
            return false;
        };
        return $.fn[pluginName] = function (options) {
            return this.each(function () {
                if (!$.data(this, "plugin_" + pluginName)) {
                    return $.data(this, "plugin_" + pluginName, new Plugin(this, options));
                }
            });
        };
    })(jQuery, window, document);

}).call(this);









/*
 * MIXITUP - A CSS3 and JQuery Filter & Sort Plugin
 * Version: 1.5.5
 * License: Creative Commons Attribution-NoDerivs 3.0 Unported - CC BY-ND 3.0
 * http://creativecommons.org/licenses/by-nd/3.0/
 * This software may be used freely on commercial and non-commercial projects with attribution to the author/copyright holder.
 * Author: Patrick Kunka
 * Copyright 2012-2013 Patrick Kunka, Barrel LLC, All Rights Reserved
 * 
 * http://mixitup.io
 */

(function ($) {

    // DECLARE METHODS

    var methods = {

        // "INIT" METHOD

        init: function (settings) {

            return this.each(function () {

                var browser = window.navigator.appVersion.match(/Chrome\/(\d+)\./),
                        ver = browser ? parseInt(browser[1], 10) : false,
                        chromeFix = function (id) {
                            var grid = document.getElementById(id),
                                    parent = grid.parentElement,
                                    placeholder = document.createElement('div'),
                                    frag = document.createDocumentFragment();

                            parent.insertBefore(placeholder, grid);
                            frag.appendChild(grid);
                            parent.replaceChild(grid, placeholder);
                            frag = null;
                            placeholder = null;
                        };

                if (ver && ver == 31 || ver == 32) {
                    chromeFix(this.id);
                }
                ;

                // BUILD CONFIG OBJECT

                var config = {

                    // PUBLIC PROPERTIES

                    targetSelector: '.mix',
                    filterSelector: '.filter',
                    sortSelector: '.sort',
                    buttonEvent: 'click',
                    effects: ['fade', 'scale'],
                    listEffects: null,
                    easing: 'smooth',
                    layoutMode: 'grid',
                    targetDisplayGrid: 'inline-block',
                    targetDisplayList: 'block',
                    listClass: '',
                    gridClass: '',
                    transitionSpeed: 600,
                    showOnLoad: 'all',
                    sortOnLoad: false,
                    multiFilter: false,
                    filterLogic: 'or',
                    resizeContainer: true,
                    minHeight: 0,
                    failClass: 'fail',
                    perspectiveDistance: '3000',
                    perspectiveOrigin: '50% 50%',
                    animateGridList: true,
                    onMixLoad: null,
                    onMixStart: null,
                    onMixEnd: null,

                    // MISC

                    container: null,
                    origOrder: [],
                    startOrder: [],
                    newOrder: [],
                    origSort: [],
                    checkSort: [],
                    filter: '',
                    mixing: false,
                    origDisplay: '',
                    origLayout: '',
                    origHeight: 0,
                    newHeight: 0,
                    isTouch: false,
                    resetDelay: 0,
                    failsafe: null,

                    // CSS

                    prefix: '',
                    easingFallback: 'ease-in-out',
                    transition: {},
                    perspective: {},
                    clean: {},
                    fade: '1',
                    scale: '',
                    rotateX: '',
                    rotateY: '',
                    rotateZ: '',
                    blur: '',
                    grayscale: ''
                };

                if (settings) {
                    $.extend(config, settings);
                }
                ;

                // ADD CONFIG OBJECT TO CONTAINER OBJECT PER INSTANTIATION

                this.config = config;

                // DETECT TOUCH

                $.support.touch = 'ontouchend' in document;

                if ($.support.touch) {
                    config.isTouch = true;
                    config.resetDelay = 350;
                }
                ;

                // LOCALIZE CONTAINER

                config.container = $(this);
                var $cont = config.container;

                // GET VENDOR PREFIX

                config.prefix = prefix($cont[0]);
                config.prefix = config.prefix ? '-' + config.prefix.toLowerCase() + '-' : '';

                // CACHE 'DEFAULT' SORTING ORDER

                $cont.find(config.targetSelector).each(function () {
                    config.origOrder.push($(this));
                });

                // PERFORM SORT ON LOAD 

                if (config.sortOnLoad) {
                    var sortby, order;
                    if ($.isArray(config.sortOnLoad)) {
                        sortby = config.sortOnLoad[0], order = config.sortOnLoad[1];
                        $(config.sortSelector + '[data-sort=' + config.sortOnLoad[0] + '][data-order=' + config.sortOnLoad[1] + ']').addClass('active');
                    } else {
                        $(config.sortSelector + '[data-sort=' + config.sortOnLoad + ']').addClass('active');
                        sortby = config.sortOnLoad, config.sortOnLoad = 'desc';
                    }
                    ;
                    sort(sortby, order, $cont, config);
                }
                ;

                // BUILD TRANSITION AND PERSPECTIVE OBJECTS

                for (var i = 0; i < 2; i++) {
                    var a = i == 0 ? a = config.prefix : '';
                    config.transition[a + 'transition'] = 'all ' + config.transitionSpeed + 'ms ease-in-out';
                    config.perspective[a + 'perspective'] = config.perspectiveDistance + 'px';
                    config.perspective[a + 'perspective-origin'] = config.perspectiveOrigin;
                }
                ;

                // BUILD TRANSITION CLEANER

                for (var i = 0; i < 2; i++) {
                    var a = i == 0 ? a = config.prefix : '';
                    config.clean[a + 'transition'] = 'none';
                }
                ;

                // CHOOSE GRID OR LIST

                if (config.layoutMode == 'list') {
                    $cont.addClass(config.listClass);
                    config.origDisplay = config.targetDisplayList;
                } else {
                    $cont.addClass(config.gridClass);
                    config.origDisplay = config.targetDisplayGrid;
                }
                ;
                config.origLayout = config.layoutMode;

                // PARSE 'SHOWONLOAD'

                var showOnLoadArray = config.showOnLoad.split(' ');

                // GIVE ACTIVE FILTER ACTIVE CLASS

                $.each(showOnLoadArray, function () {
                    $(config.filterSelector + '[data-filter="' + this + '"]').addClass('active');
                });

                // RENAME "ALL" CATEGORY TO "MIX_ALL"

                $cont.find(config.targetSelector).addClass('mix_all');
                if (showOnLoadArray[0] == 'all') {
                    showOnLoadArray[0] = 'mix_all',
                            config.showOnLoad = 'mix_all';
                }
                ;

                // FADE IN 'SHOWONLOAD'

                var $showOnLoad = $();
                $.each(showOnLoadArray, function () {
                    $showOnLoad = $showOnLoad.add($('.' + this))
                });

                $showOnLoad.each(function () {
                    var $t = $(this);
                    if (config.layoutMode == 'list') {
                        $t.css('display', config.targetDisplayList);
                    } else {
                        $t.css('display', config.targetDisplayGrid);
                    }
                    ;
                    $t.css(config.transition);
                });

                // WRAP FADE-IN TO PREVENT RACE CONDITION

                var delay = setTimeout(function () {

                    config.mixing = true;

                    $showOnLoad.css('opacity', '1');

                    // CLEAN UP

                    var reset = setTimeout(function () {
                        if (config.layoutMode == 'list') {
                            $showOnLoad.removeStyle(config.prefix + 'transition, transition').css({
                                display: config.targetDisplayList,
                                opacity: 1
                            });
                        } else {
                            $showOnLoad.removeStyle(config.prefix + 'transition, transition').css({
                                display: config.targetDisplayGrid,
                                opacity: 1
                            });
                        }
                        ;

                        // FIRE "ONMIXLOAD" CALLBACK

                        config.mixing = false;

                        if (typeof config.onMixLoad == 'function') {
                            var output = config.onMixLoad.call(this, config);

                            // UPDATE CONFIG IF DATA RETURNED

                            config = output ? output : config;
                        }
                        ;

                    }, config.transitionSpeed);
                }, 10);

                // PRESET ACTIVE FILTER

                config.filter = config.showOnLoad;

                // BIND SORT CLICK HANDLERS

                $(config.sortSelector).bind(config.buttonEvent, function () {

                    if (!config.mixing) {

                        // PARSE SORT ARGUMENTS FROM BUTTON CLASSES

                        var $t = $(this),
                                sortby = $t.attr('data-sort'),
                                order = $t.attr('data-order');

                        if (!$t.hasClass('active')) {
                            $(config.sortSelector).removeClass('active');
                            $t.addClass('active');
                        } else {
                            if (sortby != 'random')
                                return false;
                        }
                        ;

                        $cont.find(config.targetSelector).each(function () {
                            config.startOrder.push($(this));
                        });

                        goMix(config.filter, sortby, order, $cont, config);

                    }
                    ;

                });

                // BIND FILTER CLICK HANDLERS

                $(config.filterSelector).bind(config.buttonEvent, function () {

                    if (!config.mixing) {

                        var $t = $(this);

                        // PARSE FILTER ARGUMENTS FROM BUTTON CLASSES

                        if (config.multiFilter == false) {

                            // SINGLE ACTIVE BUTTON

                            $(config.filterSelector).removeClass('active');
                            $t.addClass('active');

                            config.filter = $t.attr('data-filter');

                            $(config.filterSelector + '[data-filter="' + config.filter + '"]').addClass('active');

                        } else {

                            // MULTIPLE ACTIVE BUTTONS

                            var thisFilter = $t.attr('data-filter');

                            if ($t.hasClass('active')) {
                                $t.removeClass('active');

                                // REMOVE FILTER FROM SPACE-SEPERATED STRING

                                var re = new RegExp('(\\s|^)' + thisFilter);
                                config.filter = config.filter.replace(re, '');
                            } else {

                                // ADD FILTER TO SPACE-SEPERATED STRING

                                $t.addClass('active');
                                config.filter = config.filter + ' ' + thisFilter;

                            }
                            ;
                        }
                        ;

                        // GO MIX

                        goMix(config.filter, null, null, $cont, config);

                    }
                    ;

                });

            });
        },

        // "TOGRID" METHOD

        toGrid: function () {
            return this.each(function () {
                var config = this.config;
                if (config.layoutMode != 'grid') {
                    config.layoutMode = 'grid';
                    goMix(config.filter, null, null, $(this), config);
                }
                ;
            });
        },

        // "TOLIST" METHOD

        toList: function () {
            return this.each(function () {
                var config = this.config;
                if (config.layoutMode != 'list') {
                    config.layoutMode = 'list';
                    goMix(config.filter, null, null, $(this), config);
                }
                ;
            });
        },

        // "FILTER" METHOD

        filter: function (arg) {
            return this.each(function () {
                var config = this.config;
                if (!config.mixing) {
                    $(config.filterSelector).removeClass('active');
                    $(config.filterSelector + '[data-filter="' + arg + '"]').addClass('active');
                    goMix(arg, null, null, $(this), config);
                }
                ;
            });
        },

        // "SORT" METHOD

        sort: function (args) {
            return this.each(function () {
                var config = this.config,
                        $t = $(this);
                if (!config.mixing) {
                    $(config.sortSelector).removeClass('active');
                    if ($.isArray(args)) {
                        var sortby = args[0], order = args[1];
                        $(config.sortSelector + '[data-sort="' + args[0] + '"][data-order="' + args[1] + '"]').addClass('active');
                    } else {
                        $(config.sortSelector + '[data-sort="' + args + '"]').addClass('active');
                        var sortby = args, order = 'desc';
                    }
                    ;
                    $t.find(config.targetSelector).each(function () {
                        config.startOrder.push($(this));
                    });

                    goMix(config.filter, sortby, order, $t, config);

                }
                ;
            });
        },

        // "MULTIMIX" METHOD

        multimix: function (args) {
            return this.each(function () {
                var config = this.config,
                        $t = $(this);
                multiOut = {
                    filter: config.filter,
                    sort: null,
                    order: 'desc',
                    layoutMode: config.layoutMode
                };
                $.extend(multiOut, args);
                if (!config.mixing) {
                    $(config.filterSelector).add(config.sortSelector).removeClass('active');
                    $(config.filterSelector + '[data-filter="' + multiOut.filter + '"]').addClass('active');
                    if (typeof multiOut.sort !== 'undefined') {
                        $(config.sortSelector + '[data-sort="' + multiOut.sort + '"][data-order="' + multiOut.order + '"]').addClass('active');
                        $t.find(config.targetSelector).each(function () {
                            config.startOrder.push($(this));
                        });
                    }
                    ;
                    config.layoutMode = multiOut.layoutMode;
                    goMix(multiOut.filter, multiOut.sort, multiOut.order, $t, config);
                }
                ;
            });
        },

        // "REMIX" METHOD

        remix: function (arg) {
            return this.each(function () {
                var config = this.config,
                        $t = $(this);
                config.origOrder = [];
                $t.find(config.targetSelector).each(function () {
                    var $th = $(this);
                    $th.addClass('mix_all');
                    config.origOrder.push($th);
                });
                if (!config.mixing && typeof arg !== 'undefined') {
                    $(config.filterSelector).removeClass('active');
                    $(config.filterSelector + '[data-filter="' + arg + '"]').addClass('active');
                    goMix(arg, null, null, $t, config);
                }
                ;
            });
        }
    };

    // DECLARE PLUGIN

    $.fn.mixitup = function (method, arg) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        }
        ;
    };

    /* ==== THE MAGIC ==== */

    function goMix(filter, sortby, order, $cont, config) {

        // WE ARE NOW MIXING

        clearInterval(config.failsafe);
        config.mixing = true;

        // APPLY ARGS TO CONFIG

        config.filter = filter;

        // FIRE "ONMIXSTART" CALLBACK

        if (typeof config.onMixStart == 'function') {
            var output = config.onMixStart.call(this, config);

            // UPDATE CONFIG IF DATA RETURNED

            config = output ? output : config;
        }
        ;

        // SHORT LOCAL VARS

        var speed = config.transitionSpeed;

        // REBUILD TRANSITION AND PERSPECTIVE OBJECTS

        for (var i = 0; i < 2; i++) {
            var a = i == 0 ? a = config.prefix : '';
            config.transition[a + 'transition'] = 'all ' + speed + 'ms linear';
            config.transition[a + 'transform'] = a + 'translate3d(0,0,0)';
            config.perspective[a + 'perspective'] = config.perspectiveDistance + 'px';
            config.perspective[a + 'perspective-origin'] = config.perspectiveOrigin;
        }
        ;

        // CACHE TARGET ELEMENTS FOR QUICK ACCESS

        var mixSelector = config.targetSelector,
                $targets = $cont.find(mixSelector);

        // ADD DATA OBJECT TO EACH TARGET

        $targets.each(function () {
            this.data = {};
        });

        // RE-DEFINE CONTAINER INCASE NOT IMMEDIATE PARENT OF TARGET ELEMENTS 

        var $par = $targets.parent();

        // ADD PERSPECTIVE TO CONTAINER 

        $par.css(config.perspective);

        // SETUP EASING

        config.easingFallback = 'ease-in-out';
        if (config.easing == 'smooth')
            config.easing = 'cubic-bezier(0.25, 0.46, 0.45, 0.94)';
        if (config.easing == 'snap')
            config.easing = 'cubic-bezier(0.77, 0, 0.175, 1)';
        if (config.easing == 'windback') {
            config.easing = 'cubic-bezier(0.175, 0.885, 0.320, 1.275)',
                    config.easingFallback = 'cubic-bezier(0.175, 0.885, 0.320, 1)'; // Fall-back for old webkit, with no values > 1 or < 1
        }
        ;
        if (config.easing == 'windup') {
            config.easing = 'cubic-bezier(0.6, -0.28, 0.735, 0.045)',
                    config.easingFallback = 'cubic-bezier(0.6, 0.28, 0.735, 0.045)';
        }
        ;

        // USE LIST SPECIFIC EFFECTS IF DECLARED

        var effectsOut = config.layoutMode == 'list' && config.listEffects != null ? config.listEffects : config.effects;

        // BUILD EFFECTS STRINGS & SKIP IF IE8

        if (Array.prototype.indexOf) {
            config.fade = effectsOut.indexOf('fade') > -1 ? '0' : '';
            config.scale = effectsOut.indexOf('scale') > -1 ? 'scale(.01)' : '';
            config.rotateZ = effectsOut.indexOf('rotateZ') > -1 ? 'rotate(180deg)' : '';
            config.rotateY = effectsOut.indexOf('rotateY') > -1 ? 'rotateY(90deg)' : '';
            config.rotateX = effectsOut.indexOf('rotateX') > -1 ? 'rotateX(90deg)' : '';
            config.blur = effectsOut.indexOf('blur') > -1 ? 'blur(8px)' : '';
            config.grayscale = effectsOut.indexOf('grayscale') > -1 ? 'grayscale(100%)' : '';
        }
        ;

        // DECLARE NEW JQUERY OBJECTS FOR GROUPING

        var $show = $(),
                $hide = $(),
                filterArray = [],
                multiDimensional = false;

        // BUILD FILTER ARRAY(S)

        if (typeof filter === 'string') {

            // SINGLE DIMENSIONAL FILTERING

            filterArray = buildFilterArray(filter);

        } else {

            // MULTI DIMENSIONAL FILTERING

            multiDimensional = true;

            $.each(filter, function (i) {
                filterArray[i] = buildFilterArray(this);
            });
        }
        ;

        // "OR" LOGIC (DEFAULT)

        if (config.filterLogic == 'or') {

            if (filterArray[0] == '')
                filterArray.shift(); // IF FIRST ITEM IN ARRAY IS AN EMPTY SPACE, DELETE

            // IF NO ELEMENTS ARE DESIRED THEN HIDE ALL VISIBLE ELEMENTS

            if (filterArray.length < 1) {

                $hide = $hide.add($cont.find(mixSelector + ':visible'));

            } else {

                // ELSE CHECK EACH TARGET ELEMENT FOR ANY FILTER CATEGORY:

                $targets.each(function () {
                    var $t = $(this);
                    if (!multiDimensional) {
                        // IF HAS ANY FILTER, ADD TO "SHOW" OBJECT
                        if ($t.is('.' + filterArray.join(', .'))) {
                            $show = $show.add($t);
                            // ELSE IF HAS NO FILTERS, ADD TO "HIDE" OBJECT
                        } else {
                            $hide = $hide.add($t);
                        }
                        ;
                    } else {

                        var pass = 0;
                        // FOR EACH DIMENSION

                        $.each(filterArray, function (i) {
                            if (this.length) {
                                if ($t.is('.' + this.join(', .'))) {
                                    pass++
                                }
                                ;
                            } else if (pass > 0) {
                                pass++;
                            }
                            ;
                        });
                        // IF PASSES ALL DIMENSIONS, SHOW
                        if (pass == filterArray.length) {
                            $show = $show.add($t);
                            // ELSE HIDE
                        } else {
                            $hide = $hide.add($t);
                        }
                        ;
                    }
                    ;
                });

            }
            ;

        } else {

            // "AND" LOGIC

            // ADD "MIX_SHOW" CLASS TO ELEMENTS THAT HAVE ALL FILTERS

            $show = $show.add($par.find(mixSelector + '.' + filterArray.join('.')));

            // ADD "MIX_HIDE" CLASS TO EVERYTHING ELSE

            $hide = $hide.add($par.find(mixSelector + ':not(.' + filterArray.join('.') + '):visible'));
        }
        ;

        // GET TOTAL NUMBER OF ELEMENTS TO SHOW

        var total = $show.length;

        // DECLARE NEW JQUERY OBJECTS

        var $tohide = $(),
                $toshow = $(),
                $pre = $();

        // FOR ELEMENTS TO BE HIDDEN, IF NOT ALREADY HIDDEN THEN ADD TO OBJECTS "TOHIDE" AND "PRE" 
        // TO INDICATE PRE-EXISTING ELEMENTS TO BE HIDDEN

        $hide.each(function () {
            var $t = $(this);
            if ($t.css('display') != 'none') {
                $tohide = $tohide.add($t);
                $pre = $pre.add($t);
            }
            ;
        });

        // IF ALL ELEMENTS ARE ALREADY SHOWN AND THERE IS NOTHING TO HIDE, AND NOT PERFORMING A LAYOUT CHANGE OR SORT:

        if ($show.filter(':visible').length == total && !$tohide.length && !sortby) {

            if (config.origLayout == config.layoutMode) {

                // THEN CLEAN UP AND GO HOME

                resetFilter();
                return false;
            } else {

                // IF ONLY ONE ITEM AND CHANGING FORM GRID TO LIST, MOST LIKELY POSITION WILL NOT CHANGE SO WE'RE DONE

                if ($show.length == 1) {

                    if (config.layoutMode == 'list') {
                        $cont.addClass(config.listClass);
                        $cont.removeClass(config.gridClass);
                        $pre.css('display', config.targetDisplayList);
                    } else {
                        $cont.addClass(config.gridClass);
                        $cont.removeClass(config.listClass);
                        $pre.css('display', config.targetDisplayGrid);
                    }
                    ;

                    // THEN CLEAN UP AND GO HOME

                    resetFilter();
                    return false;
                }
            }
            ;
        }
        ;

        // GET CONTAINER'S STARTING HEIGHT

        config.origHeight = $par.height();

        // IF THERE IS SOMETHING TO BE SHOWN:

        if ($show.length) {

            // REMOVE "FAIL CLASS" FROM CONTAINER IF EXISTS

            $cont.removeClass(config.failClass);


            // FOR ELEMENTS TO BE SHOWN, IF NOT ALREADY SHOWN THEN ADD TO OBJECTS "TOSHOW" ELSE ADD CLASS "MIX_PRE"
            // TO INDICATE PRE-EXISTING ELEMENT

            $show.each(function () {
                var $t = $(this);
                if ($t.css('display') == 'none') {
                    $toshow = $toshow.add($t)
                } else {
                    $pre = $pre.add($t);
                }
                ;
            });

            // IF NON-ANIMATED LAYOUT MODE TRANSITION:

            if ((config.origLayout != config.layoutMode) && config.animateGridList == false) {

                // ADD NEW DISPLAY TYPES, CLEAN UP AND GO HOME

                if (config.layoutMode == 'list') {
                    $cont.addClass(config.listClass);
                    $cont.removeClass(config.gridClass);
                    $pre.css('display', config.targetDisplayList);
                } else {
                    $cont.addClass(config.gridClass);
                    $cont.removeClass(config.listClass);
                    $pre.css('display', config.targetDisplayGrid);
                }
                ;

                resetFilter();
                return false;
            }
            ;

            // IF IE, FUCK OFF, AND THEN CLEAN UP AND GO HOME

            if (!window.atob) {
                resetFilter();
                return false;
            }
            ;

            // OVERRIDE ANY EXISTING TRANSITION TIMING FOR CALCULATIONS

            $targets.css(config.clean);

            // FOR EACH PRE-EXISTING ELEMENT, ADD STARTING POSITION TO 'ORIGPOS' ARRAY

            $pre.each(function () {
                this.data.origPos = $(this).offset();
            });

            // TEMPORARILY SHOW ALL ELEMENTS TO SHOW (THAT ARE NOT ALREADY SHOWN), WITHOUT HIDING ELEMENTS TO HIDE
            // AND ADD/REMOVE GRID AND LIST CLASSES FROM CONTAINER

            if (config.layoutMode == 'list') {
                $cont.addClass(config.listClass);
                $cont.removeClass(config.gridClass);
                $toshow.css('display', config.targetDisplayList);
            } else {
                $cont.addClass(config.gridClass);
                $cont.removeClass(config.listClass);
                $toshow.css('display', config.targetDisplayGrid);
            }
            ;

            // FOR EACH ELEMENT NOW SHOWN, ADD ITS INTERMEDIATE POSITION TO 'SHOWINTERPOS' ARRAY

            $toshow.each(function () {
                this.data.showInterPos = $(this).offset();
            });

            // FOR EACH ELEMENT TO BE HIDDEN, BUT NOT YET HIDDEN, AND NOW MOVED DUE TO SHOWN ELEMENTS,
            // ADD ITS INTERMEDIATE POSITION TO 'HIDEINTERPOS' ARRAY

            $tohide.each(function () {
                this.data.hideInterPos = $(this).offset();
            });

            // FOR EACH PRE-EXISTING ELEMENT, NOW MOVED DUE TO SHOWN ELEMENTS, ADD ITS POSITION TO 'PREINTERPOS' ARRAY

            $pre.each(function () {
                this.data.preInterPos = $(this).offset();
            });

            // SET DISPLAY PROPERTY OF PRE-EXISTING ELEMENTS INCASE WE ARE CHANGING LAYOUT MODE

            if (config.layoutMode == 'list') {
                $pre.css('display', config.targetDisplayList);
            } else {
                $pre.css('display', config.targetDisplayGrid);
            }
            ;

            // IF A SORT ARGUMENT HAS BEEN SENT, RUN SORT FUNCTION SO OBJECTS WILL MOVE TO THEIR FINAL ORDER

            if (sortby) {
                sort(sortby, order, $cont, config);
            }
            ;

            // IF VISIBLE SORT ORDER IS THE SAME (WHICH WOULD NOT TRIGGER A TRANSITION EVENT)

            if (sortby && compareArr(config.origSort, config.checkSort)) {

                // THEN CLEAN UP AND GO HOME
                resetFilter();
                return false;
            }
            ;

            // TEMPORARILY HIDE ALL SHOWN ELEMENTS TO HIDE

            $tohide.hide();

            // FOR EACH ELEMENT TO SHOW, AND NOW MOVED DUE TO HIDDEN ELEMENTS BEING REMOVED, 
            // ADD ITS POSITION TO 'FINALPOS' ARRAY

            $toshow.each(function (i) {
                this.data.finalPos = $(this).offset();
            });

            // FOR EACH PRE-EXISTING ELEMENT NOW MOVED DUE TO HIDDEN ELEMENTS BEING REMOVED,
            // ADD ITS POSITION TO 'FINALPREPOS' ARRAY

            $pre.each(function () {
                this.data.finalPrePos = $(this).offset();
            });

            // SINCE WE ARE IN OUT FINAL STATE, GET NEW HEIGHT OF CONTAINER

            config.newHeight = $par.height();

            // IF A SORT ARGUMENT AS BEEN SENT, RUN SORT FUNCTION 'RESET' TO MOVE ELEMENTS BACK TO THEIR STARTING ORDER

            if (sortby) {
                sort('reset', null, $cont, config);
            }
            ;

            // RE-HIDE ALL ELEMENTS TEMPORARILY SHOWN

            $toshow.hide();

            // SET DISPLAY PROPERTY OF PRE-EXISTING ELEMENTS BACK TO THEIR 
            // ORIGINAL PROPERTY, INCASE WE ARE CHANGING LAYOUT MODE

            $pre.css('display', config.origDisplay);

            // ADD/REMOVE GRID AND LIST CLASSES FROM CONTAINER

            if (config.origDisplay == 'block') {
                $cont.addClass(config.listClass);
                $toshow.css('display', config.targetDisplayList);
            } else {
                $cont.removeClass(config.listClass);
                $toshow.css('display', config.targetDisplayGrid);
            }
            ;

            // IF WE ARE ANIMATING CONTAINER, RESET IT TO ITS STARTING HEIGHT

            if (config.resizeContainer)
                $par.css('height', config.origHeight + 'px');

            // ADD TRANSFORMS TO ALL ELEMENTS TO SHOW

            var toShowCSS = {};

            for (var i = 0; i < 2; i++) {
                var a = i == 0 ? a = config.prefix : '';
                toShowCSS[a + 'transform'] = config.scale + ' ' + config.rotateX + ' ' + config.rotateY + ' ' + config.rotateZ;
                toShowCSS[a + 'filter'] = config.blur + ' ' + config.grayscale;
            }
            ;

            $toshow.css(toShowCSS);

            // FOR EACH PRE-EXISTING ELEMENT, SUBTRACT ITS INTERMEDIATE POSITION FROM ITS ORIGINAL POSITION 
            // TO GET ITS STARTING OFFSET

            $pre.each(function () {
                var data = this.data,
                        $t = $(this);

                if ($t.hasClass('mix_tohide')) {
                    data.preTX = data.origPos.left - data.hideInterPos.left;
                    data.preTY = data.origPos.top - data.hideInterPos.top;
                } else {
                    data.preTX = data.origPos.left - data.preInterPos.left;
                    data.preTY = data.origPos.top - data.preInterPos.top;
                }
                ;
                var preCSS = {};
                for (var i = 0; i < 2; i++) {
                    var a = i == 0 ? a = config.prefix : '';
                    preCSS[a + 'transform'] = 'translate(' + data.preTX + 'px,' + data.preTY + 'px)';
                }
                ;

                $t.css(preCSS);
            });

            // ADD/REMOVE GRID AND LIST CLASSES FROM CONTAINER

            if (config.layoutMode == 'list') {
                $cont.addClass(config.listClass);
                $cont.removeClass(config.gridClass);
            } else {
                $cont.addClass(config.gridClass);
                $cont.removeClass(config.listClass);
            }
            ;

            // WRAP ANIMATION FUNCTIONS IN 10ms TIMEOUT TO PREVENT RACE CONDITION

            var delay = setTimeout(function () {

                // APPLY TRANSITION TIMING TO CONTAINER, AND BEGIN ANIMATION TO NEW HEIGHT

                if (config.resizeContainer) {
                    var containerCSS = {};
                    for (var i = 0; i < 2; i++) {
                        var a = i == 0 ? a = config.prefix : '';
                        containerCSS[a + 'transition'] = 'all ' + speed + 'ms ease-in-out';
                        containerCSS['height'] = config.newHeight + 'px';
                    }
                    ;
                    $par.css(containerCSS);
                }
                ;

                // BEGIN FADING IN/OUT OF ALL ELEMENTS TO SHOW/HIDE
                $tohide.css('opacity', config.fade);
                $toshow.css('opacity', 1);

                // FOR EACH ELEMENT BEING SHOWN, CALCULATE ITS TRAJECTORY BY SUBTRACTING
                // ITS INTERMEDIATE POSITION FROM ITS FINAL POSITION.
                // ALSO ADD SPEED AND EASING

                $toshow.each(function () {
                    var data = this.data;
                    data.tX = data.finalPos.left - data.showInterPos.left;
                    data.tY = data.finalPos.top - data.showInterPos.top;

                    var toShowCSS = {};
                    for (var i = 0; i < 2; i++) {
                        var a = i == 0 ? a = config.prefix : '';
                        toShowCSS[a + 'transition-property'] = a + 'transform, ' + a + 'filter, opacity';
                        toShowCSS[a + 'transition-timing-function'] = config.easing + ', linear, linear';
                        toShowCSS[a + 'transition-duration'] = speed + 'ms';
                        toShowCSS[a + 'transition-delay'] = '0';
                        toShowCSS[a + 'transform'] = 'translate(' + data.tX + 'px,' + data.tY + 'px)';
                        toShowCSS[a + 'filter'] = 'none';
                    }
                    ;

                    $(this).css('-webkit-transition', 'all ' + speed + 'ms ' + config.easingFallback).css(toShowCSS);
                });

                // FOR EACH PRE-EXISTING ELEMENT, IF IT HAS A FINAL POSITION, CALCULATE 
                // ITS TRAJETORY BY SUBTRACTING ITS INTERMEDIATE POSITION FROM ITS FINAL POSITION.
                // ALSO ADD SPEED AND EASING

                $pre.each(function () {
                    var data = this.data
                    data.tX = data.finalPrePos.left != 0 ? data.finalPrePos.left - data.preInterPos.left : 0;
                    data.tY = data.finalPrePos.left != 0 ? data.finalPrePos.top - data.preInterPos.top : 0;

                    var preCSS = {};
                    for (var i = 0; i < 2; i++) {
                        var a = i == 0 ? a = config.prefix : '';
                        preCSS[a + 'transition'] = 'all ' + speed + 'ms ' + config.easing;
                        preCSS[a + 'transform'] = 'translate(' + data.tX + 'px,' + data.tY + 'px)';
                    }
                    ;

                    $(this).css('-webkit-transition', 'all ' + speed + 'ms ' + config.easingFallback).css(preCSS);
                });

                // BEGIN TRANSFORMS ON ALL ELEMENTS TO BE HIDDEN

                var toHideCSS = {};
                for (var i = 0; i < 2; i++) {
                    var a = i == 0 ? a = config.prefix : '';
                    toHideCSS[a + 'transition'] = 'all ' + speed + 'ms ' + config.easing + ', ' + a + 'filter ' + speed + 'ms linear, opacity ' + speed + 'ms linear';
                    toHideCSS[a + 'transform'] = config.scale + ' ' + config.rotateX + ' ' + config.rotateY + ' ' + config.rotateZ;
                    toHideCSS[a + 'filter'] = config.blur + ' ' + config.grayscale;
                    toHideCSS['opacity'] = config.fade;
                }
                ;

                $tohide.css(toHideCSS);

                // ALL ANIMATIONS HAVE NOW BEEN STARTED, NOW LISTEN FOR TRANSITION END:

                $par.bind('webkitTransitionEnd transitionend otransitionend oTransitionEnd', function (e) {

                    if (e.originalEvent.propertyName.indexOf('transform') > -1 || e.originalEvent.propertyName.indexOf('opacity') > -1) {

                        if (mixSelector.indexOf('.') > -1) {

                            // IF MIXSELECTOR IS A CLASS NAME

                            if ($(e.target).hasClass(mixSelector.replace('.', ''))) {
                                resetFilter();
                            }
                            ;

                        } else {

                            // IF MIXSELECTOR IS A TAG

                            if ($(e.target).is(mixSelector)) {
                                resetFilter();
                            }
                            ;

                        }
                        ;

                    }
                    ;
                });

            }, 10);

            // LAST RESORT EMERGENCY FAILSAFE

            config.failsafe = setTimeout(function () {
                if (config.mixing) {
                    resetFilter();
                }
                ;
            }, speed + 400);

        } else {

            // ELSE IF NOTHING TO SHOW, AND EVERYTHING TO BE HIDDEN

            // IF WE ARE RESIZING CONTAINER, SET ITS STARTING HEIGHT

            if (config.resizeContainer)
                $par.css('height', config.origHeight + 'px');

            // IF IE, FUCK OFF, AND THEN GO HOME

            if (!window.atob) {
                resetFilter();
                return false;
            }
            ;

            // GROUP ALL ELEMENTS TO HIDE INTO JQUERY OBJECT

            $tohide = $hide;

            // WRAP ANIMATION FUNCTIONS IN A 10ms DELAY TO PREVENT RACE CONDITION

            var delay = setTimeout(function () {

                // APPLY PERSPECTIVE TO CONTAINER

                $par.css(config.perspective);

                // APPLY TRANSITION TIMING TO CONTAINER, AND BEGIN ANIMATION TO NEW HEIGHT

                if (config.resizeContainer) {
                    var containerCSS = {};
                    for (var i = 0; i < 2; i++) {
                        var a = i == 0 ? a = config.prefix : '';
                        containerCSS[a + 'transition'] = 'height ' + speed + 'ms ease-in-out';
                        containerCSS['height'] = config.minHeight + 'px';
                    }
                    ;
                    $par.css(containerCSS);
                }
                ;

                // APPLY TRANSITION TIMING TO ALL TARGET ELEMENTS

                $targets.css(config.transition);

                // GET TOTAL NUMBER OF ELEMENTS TO HIDE

                var totalHide = $hide.length;

                // IF SOMETHING TO HIDE:

                if (totalHide) {

                    // BEGIN TRANSFORMS ON ALL ELEMENTS TO BE HIDDEN

                    var toHideCSS = {};
                    for (var i = 0; i < 2; i++) {
                        var a = i == 0 ? a = config.prefix : '';
                        toHideCSS[a + 'transform'] = config.scale + ' ' + config.rotateX + ' ' + config.rotateY + ' ' + config.rotateZ;
                        toHideCSS[a + 'filter'] = config.blur + ' ' + config.grayscale;
                        toHideCSS['opacity'] = config.fade;
                    }
                    ;

                    $tohide.css(toHideCSS);

                    // ALL ANIMATIONS HAVE NOW BEEN STARTED, NOW LISTEN FOR TRANSITION END:

                    $par.bind('webkitTransitionEnd transitionend otransitionend oTransitionEnd', function (e) {
                        if (e.originalEvent.propertyName.indexOf('transform') > -1 || e.originalEvent.propertyName.indexOf('opacity') > -1) {
                            $cont.addClass(config.failClass);
                            resetFilter();
                        }
                        ;
                    });

                } else {

                    // ELSE, WE'RE DONE MIXING

                    config.mixing = false;
                }
                ;

            }, 10);
        }
        ;

        // CLEAN UP AND RESET FUNCTION

        function resetFilter() {

            // UNBIND TRANSITION END EVENTS FROM CONTAINER

            $par.unbind('webkitTransitionEnd transitionend otransitionend oTransitionEnd');

            // IF A SORT ARGUMENT HAS BEEN SENT, SORT ELEMENTS TO THEIR FINAL ORDER

            if (sortby) {
                sort(sortby, order, $cont, config);
            }
            ;

            // EMPTY SORTING ARRAYS

            config.startOrder = [], config.newOrder = [], config.origSort = [], config.checkSort = [];

            // REMOVE INLINE STYLES FROM ALL TARGET ELEMENTS AND SLAM THE BRAKES ON

            $targets.removeStyle(
                    config.prefix + 'filter, filter, ' + config.prefix + 'transform, transform, opacity, display'
                    ).css(config.clean).removeAttr('data-checksum');

            // BECAUSE IE SUCKS

            if (!window.atob) {
                $targets.css({
                    display: 'none',
                    opacity: '0'
                });
            }
            ;

            // REMOVE HEIGHT FROM CONTAINER ONLY IF RESIZING

            var remH = config.resizeContainer ? 'height' : '';

            // REMOVE INLINE STYLES FROM CONTAINER

            $par.removeStyle(
                    config.prefix + 'transition, transition, ' + config.prefix + 'perspective, perspective, ' + config.prefix + 'perspective-origin, perspective-origin, ' + remH
                    );

            // ADD FINAL DISPLAY PROPERTIES AND OPACITY TO ALL SHOWN ELEMENTS
            // CACHE CURRENT LAYOUT MODE & SORT FOR NEXT MIX

            if (config.layoutMode == 'list') {
                $show.css({display: config.targetDisplayList, opacity: '1'});
                config.origDisplay = config.targetDisplayList;
            } else {
                $show.css({display: config.targetDisplayGrid, opacity: '1'});
                config.origDisplay = config.targetDisplayGrid;
            }
            ;
            config.origLayout = config.layoutMode;

            var wait = setTimeout(function () {

                // LET GO OF THE BRAKES

                $targets.removeStyle(config.prefix + 'transition, transition');

                // WE'RE DONE MIXING

                config.mixing = false;

                // FIRE "ONMIXEND" CALLBACK

                if (typeof config.onMixEnd == 'function') {
                    var output = config.onMixEnd.call(this, config);

                    // UPDATE CONFIG IF DATA RETURNED

                    config = output ? output : config;
                }
                ;
            });
        }
        ;
    }
    ;

    // SORT FUNCTION

    function sort(sortby, order, $cont, config) {

        // COMPARE BY ATTRIBUTE

        function compare(a, b) {
            var sortAttrA = isNaN(a.attr(sortby) * 1) ? a.attr(sortby).toLowerCase() : a.attr(sortby) * 1,
                    sortAttrB = isNaN(b.attr(sortby) * 1) ? b.attr(sortby).toLowerCase() : b.attr(sortby) * 1;
            if (sortAttrA < sortAttrB)
                return -1;
            if (sortAttrA > sortAttrB)
                return 1;
            return 0;
        }
        ;

        // REBUILD DOM

        function rebuild(element) {
            if (order == 'asc') {
                $sortWrapper.prepend(element).prepend(' ');
            } else {
                $sortWrapper.append(element).append(' ');
            }
            ;
        }
        ;

        // RANDOMIZE ARRAY

        function arrayShuffle(oldArray) {
            var newArray = oldArray.slice();
            var len = newArray.length;
            var i = len;
            while (i--) {
                var p = parseInt(Math.random() * len);
                var t = newArray[i];
                newArray[i] = newArray[p];
                newArray[p] = t;
            }
            ;
            return newArray;
        }
        ;

        // SORT

        $cont.find(config.targetSelector).wrapAll('<div class="mix_sorter"/>');

        var $sortWrapper = $cont.find('.mix_sorter');

        if (!config.origSort.length) {
            $sortWrapper.find(config.targetSelector + ':visible').each(function () {
                $(this).wrap('<s/>');
                config.origSort.push($(this).parent().html().replace(/\s+/g, ''));
                $(this).unwrap();
            });
        }
        ;



        $sortWrapper.empty();

        if (sortby == 'reset') {
            $.each(config.startOrder, function () {
                $sortWrapper.append(this).append(' ');
            });
        } else if (sortby == 'default') {
            $.each(config.origOrder, function () {
                rebuild(this);
            });
        } else if (sortby == 'random') {
            if (!config.newOrder.length) {
                config.newOrder = arrayShuffle(config.startOrder);
            }
            ;
            $.each(config.newOrder, function () {
                $sortWrapper.append(this).append(' ');
            });
        } else if (sortby == 'custom') {
            $.each(order, function () {
                rebuild(this);
            });
        } else {
            // SORT BY ATTRIBUTE

            if (typeof config.origOrder[0].attr(sortby) === 'undefined') {
                console.log('No such attribute found. Terminating');
                return false;
            }
            ;

            if (!config.newOrder.length) {
                $.each(config.origOrder, function () {
                    config.newOrder.push($(this));
                });
                config.newOrder.sort(compare);
            }
            ;
            $.each(config.newOrder, function () {
                rebuild(this);
            });

        }
        ;
        config.checkSort = [];
        $sortWrapper.find(config.targetSelector + ':visible').each(function (i) {
            var $t = $(this);
            if (i == 0) {

                // PREVENT COMPARE RETURNING FALSE POSITIVES ON ELEMENTS WITH NO CLASS/ATTRIBUTES

                $t.attr('data-checksum', '1');
            }
            ;
            $t.wrap('<s/>');
            config.checkSort.push($t.parent().html().replace(/\s+/g, ''));
            $t.unwrap();
        });

        $cont.find(config.targetSelector).unwrap();
    }
    ;

    // FIND VENDOR PREFIX

    function prefix(el) {
        var prefixes = ["Webkit", "Moz", "O", "ms"];
        for (var i = 0; i < prefixes.length; i++) {
            if (prefixes[i] + "Transition" in el.style) {
                return prefixes[i];
            }
            ;
        }
        ;
        return "transition" in el.style ? "" : false;
    }
    ;

    // REMOVE SPECIFIC STYLES

    $.fn.removeStyle = function (style) {
        return this.each(function () {
            var obj = $(this);
            style = style.replace(/\s+/g, '');
            var styles = style.split(',');
            $.each(styles, function () {

                var search = new RegExp(this.toString() + '[^;]+;?', 'g');
                obj.attr('style', function (i, style) {
                    if (style)
                        return style.replace(search, '');
                });
            });
        });
    };

    // COMPARE ARRAYS 

    function compareArr(a, b) {
        if (a.length != b.length)
            return false;
        for (var i = 0; i < b.length; i++) {
            if (a[i].compare) {
                if (!a[i].compare(b[i]))
                    return false;
            }
            ;
            if (a[i] !== b[i])
                return false;
        }
        ;
        return true;
    }
    ;

    // BUILD FILTER ARRAY(S)

    function buildFilterArray(str) {
        // CLEAN FILTER STRING
        str = str.replace(/\s{2,}/g, ' ');
        // FOR EACH PEROID SEPERATED CLASS NAME, ADD STRING TO FILTER ARRAY
        var arr = str.split(' ');
        // IF ALL, REPLACE WITH MIX_ALL
        $.each(arr, function (i) {
            if (this == 'all')
                arr[i] = 'mix_all';
        });
        if (arr[0] == "")
            arr.shift();
        return arr;
    }
    ;


})(jQuery);



































/*!
 * Isotope PACKAGED v3.0.1
 *
 * Licensed GPLv3 for open source use
 * or Isotope Commercial License for commercial use
 *
 * http://isotope.metafizzy.co
 * Copyright 2016 Metafizzy
 */

!function (t, e) {
    "use strict";
    "function" == typeof define && define.amd ? define("jquery-bridget/jquery-bridget", ["jquery"], function (i) {
        e(t, i)
    }) : "object" == typeof module && module.exports ? module.exports = e(t, require("jquery")) : t.jQueryBridget = e(t, t.jQuery)
}(window, function (t, e) {
    "use strict";
    function i(i, s, a) {
        function u(t, e, n) {
            var o, s = "$()." + i + '("' + e + '")';
            return t.each(function (t, u) {
                var h = a.data(u, i);
                if (!h)
                    return void r(i + " not initialized. Cannot call methods, i.e. " + s);
                var d = h[e];
                if (!d || "_" == e.charAt(0))
                    return void r(s + " is not a valid method");
                var l = d.apply(h, n);
                o = void 0 === o ? l : o
            }), void 0 !== o ? o : t
        }
        function h(t, e) {
            t.each(function (t, n) {
                var o = a.data(n, i);
                o ? (o.option(e), o._init()) : (o = new s(n, e), a.data(n, i, o))
            })
        }
        a = a || e || t.jQuery, a && (s.prototype.option || (s.prototype.option = function (t) {
            a.isPlainObject(t) && (this.options = a.extend(!0, this.options, t))
        }), a.fn[i] = function (t) {
            if ("string" == typeof t) {
                var e = o.call(arguments, 1);
                return u(this, t, e)
            }
            return h(this, t), this
        }, n(a))
    }
    function n(t) {
        !t || t && t.bridget || (t.bridget = i)
    }
    var o = Array.prototype.slice, s = t.console, r = "undefined" == typeof s ? function () {} : function (t) {
        s.error(t)
    };
    return n(e || t.jQuery), i
}), function (t, e) {
    "function" == typeof define && define.amd ? define("ev-emitter/ev-emitter", e) : "object" == typeof module && module.exports ? module.exports = e() : t.EvEmitter = e()
}("undefined" != typeof window ? window : this, function () {
    function t() {}
    var e = t.prototype;
    return e.on = function (t, e) {
        if (t && e) {
            var i = this._events = this._events || {}, n = i[t] = i[t] || [];
            return-1 == n.indexOf(e) && n.push(e), this
        }
    }, e.once = function (t, e) {
        if (t && e) {
            this.on(t, e);
            var i = this._onceEvents = this._onceEvents || {}, n = i[t] = i[t] || {};
            return n[e] = !0, this
        }
    }, e.off = function (t, e) {
        var i = this._events && this._events[t];
        if (i && i.length) {
            var n = i.indexOf(e);
            return-1 != n && i.splice(n, 1), this
        }
    }, e.emitEvent = function (t, e) {
        var i = this._events && this._events[t];
        if (i && i.length) {
            var n = 0, o = i[n];
            e = e || [];
            for (var s = this._onceEvents && this._onceEvents[t]; o; ) {
                var r = s && s[o];
                r && (this.off(t, o), delete s[o]), o.apply(this, e), n += r ? 0 : 1, o = i[n]
            }
            return this
        }
    }, t
}), function (t, e) {
    "use strict";
    "function" == typeof define && define.amd ? define("get-size/get-size", [], function () {
        return e()
    }) : "object" == typeof module && module.exports ? module.exports = e() : t.getSize = e()
}(window, function () {
    "use strict";
    function t(t) {
        var e = parseFloat(t), i = -1 == t.indexOf("%") && !isNaN(e);
        return i && e
    }
    function e() {}
    function i() {
        for (var t = {width: 0, height: 0, innerWidth: 0, innerHeight: 0, outerWidth: 0, outerHeight: 0}, e = 0; h > e; e++) {
            var i = u[e];
            t[i] = 0
        }
        return t
    }
    function n(t) {
        var e = getComputedStyle(t);
        return e || a("Style returned " + e + ". Are you running this code in a hidden iframe on Firefox? See http://bit.ly/getsizebug1"), e
    }
    function o() {
        if (!d) {
            d = !0;
            var e = document.createElement("div");
            e.style.width = "200px", e.style.padding = "1px 2px 3px 4px", e.style.borderStyle = "solid", e.style.borderWidth = "1px 2px 3px 4px", e.style.boxSizing = "border-box";
            var i = document.body || document.documentElement;
            i.appendChild(e);
            var o = n(e);
            s.isBoxSizeOuter = r = 200 == t(o.width), i.removeChild(e)
        }
    }
    function s(e) {
        if (o(), "string" == typeof e && (e = document.querySelector(e)), e && "object" == typeof e && e.nodeType) {
            var s = n(e);
            if ("none" == s.display)
                return i();
            var a = {};
            a.width = e.offsetWidth, a.height = e.offsetHeight;
            for (var d = a.isBorderBox = "border-box" == s.boxSizing, l = 0; h > l; l++) {
                var f = u[l], c = s[f], m = parseFloat(c);
                a[f] = isNaN(m) ? 0 : m
            }
            var p = a.paddingLeft + a.paddingRight, y = a.paddingTop + a.paddingBottom, g = a.marginLeft + a.marginRight, v = a.marginTop + a.marginBottom, _ = a.borderLeftWidth + a.borderRightWidth, I = a.borderTopWidth + a.borderBottomWidth, z = d && r, x = t(s.width);
            x !== !1 && (a.width = x + (z ? 0 : p + _));
            var S = t(s.height);
            return S !== !1 && (a.height = S + (z ? 0 : y + I)), a.innerWidth = a.width - (p + _), a.innerHeight = a.height - (y + I), a.outerWidth = a.width + g, a.outerHeight = a.height + v, a
        }
    }
    var r, a = "undefined" == typeof console ? e : function (t) {
        console.error(t)
    }, u = ["paddingLeft", "paddingRight", "paddingTop", "paddingBottom", "marginLeft", "marginRight", "marginTop", "marginBottom", "borderLeftWidth", "borderRightWidth", "borderTopWidth", "borderBottomWidth"], h = u.length, d = !1;
    return s
}), function (t, e) {
    "use strict";
    "function" == typeof define && define.amd ? define("desandro-matches-selector/matches-selector", e) : "object" == typeof module && module.exports ? module.exports = e() : t.matchesSelector = e()
}(window, function () {
    "use strict";
    var t = function () {
        var t = Element.prototype;
        if (t.matches)
            return"matches";
        if (t.matchesSelector)
            return"matchesSelector";
        for (var e = ["webkit", "moz", "ms", "o"], i = 0; i < e.length; i++) {
            var n = e[i], o = n + "MatchesSelector";
            if (t[o])
                return o
        }
    }();
    return function (e, i) {
        return e[t](i)
    }
}), function (t, e) {
    "function" == typeof define && define.amd ? define("fizzy-ui-utils/utils", ["desandro-matches-selector/matches-selector"], function (i) {
        return e(t, i)
    }) : "object" == typeof module && module.exports ? module.exports = e(t, require("desandro-matches-selector")) : t.fizzyUIUtils = e(t, t.matchesSelector)
}(window, function (t, e) {
    var i = {};
    i.extend = function (t, e) {
        for (var i in e)
            t[i] = e[i];
        return t
    }, i.modulo = function (t, e) {
        return(t % e + e) % e
    }, i.makeArray = function (t) {
        var e = [];
        if (Array.isArray(t))
            e = t;
        else if (t && "number" == typeof t.length)
            for (var i = 0; i < t.length; i++)
                e.push(t[i]);
        else
            e.push(t);
        return e
    }, i.removeFrom = function (t, e) {
        var i = t.indexOf(e);
        -1 != i && t.splice(i, 1)
    }, i.getParent = function (t, i) {
        for (; t != document.body; )
            if (t = t.parentNode, e(t, i))
                return t
    }, i.getQueryElement = function (t) {
        return"string" == typeof t ? document.querySelector(t) : t
    }, i.handleEvent = function (t) {
        var e = "on" + t.type;
        this[e] && this[e](t)
    }, i.filterFindElements = function (t, n) {
        t = i.makeArray(t);
        var o = [];
        return t.forEach(function (t) {
            if (t instanceof HTMLElement) {
                if (!n)
                    return void o.push(t);
                e(t, n) && o.push(t);
                for (var i = t.querySelectorAll(n), s = 0; s < i.length; s++)
                    o.push(i[s])
            }
        }), o
    }, i.debounceMethod = function (t, e, i) {
        var n = t.prototype[e], o = e + "Timeout";
        t.prototype[e] = function () {
            var t = this[o];
            t && clearTimeout(t);
            var e = arguments, s = this;
            this[o] = setTimeout(function () {
                n.apply(s, e), delete s[o]
            }, i || 100)
        }
    }, i.docReady = function (t) {
        var e = document.readyState;
        "complete" == e || "interactive" == e ? t() : document.addEventListener("DOMContentLoaded", t)
    }, i.toDashed = function (t) {
        return t.replace(/(.)([A-Z])/g, function (t, e, i) {
            return e + "-" + i
        }).toLowerCase()
    };
    var n = t.console;
    return i.htmlInit = function (e, o) {
        i.docReady(function () {
            var s = i.toDashed(o), r = "data-" + s, a = document.querySelectorAll("[" + r + "]"), u = document.querySelectorAll(".js-" + s), h = i.makeArray(a).concat(i.makeArray(u)), d = r + "-options", l = t.jQuery;
            h.forEach(function (t) {
                var i, s = t.getAttribute(r) || t.getAttribute(d);
                try {
                    i = s && JSON.parse(s)
                } catch (a) {
                    return void(n && n.error("Error parsing " + r + " on " + t.className + ": " + a))
                }
                var u = new e(t, i);
                l && l.data(t, o, u)
            })
        })
    }, i
}), function (t, e) {
    "function" == typeof define && define.amd ? define("outlayer/item", ["ev-emitter/ev-emitter", "get-size/get-size"], e) : "object" == typeof module && module.exports ? module.exports = e(require("ev-emitter"), require("get-size")) : (t.Outlayer = {}, t.Outlayer.Item = e(t.EvEmitter, t.getSize))
}(window, function (t, e) {
    "use strict";
    function i(t) {
        for (var e in t)
            return!1;
        return e = null, !0
    }
    function n(t, e) {
        t && (this.element = t, this.layout = e, this.position = {x: 0, y: 0}, this._create())
    }
    function o(t) {
        return t.replace(/([A-Z])/g, function (t) {
            return"-" + t.toLowerCase()
        })
    }
    var s = document.documentElement.style, r = "string" == typeof s.transition ? "transition" : "WebkitTransition", a = "string" == typeof s.transform ? "transform" : "WebkitTransform", u = {WebkitTransition: "webkitTransitionEnd", transition: "transitionend"}[r], h = {transform: a, transition: r, transitionDuration: r + "Duration", transitionProperty: r + "Property", transitionDelay: r + "Delay"}, d = n.prototype = Object.create(t.prototype);
    d.constructor = n, d._create = function () {
        this._transn = {ingProperties: {}, clean: {}, onEnd: {}}, this.css({position: "absolute"})
    }, d.handleEvent = function (t) {
        var e = "on" + t.type;
        this[e] && this[e](t)
    }, d.getSize = function () {
        this.size = e(this.element)
    }, d.css = function (t) {
        var e = this.element.style;
        for (var i in t) {
            var n = h[i] || i;
            e[n] = t[i]
        }
    }, d.getPosition = function () {
        var t = getComputedStyle(this.element), e = this.layout._getOption("originLeft"), i = this.layout._getOption("originTop"), n = t[e ? "left" : "right"], o = t[i ? "top" : "bottom"], s = this.layout.size, r = -1 != n.indexOf("%") ? parseFloat(n) / 100 * s.width : parseInt(n, 10), a = -1 != o.indexOf("%") ? parseFloat(o) / 100 * s.height : parseInt(o, 10);
        r = isNaN(r) ? 0 : r, a = isNaN(a) ? 0 : a, r -= e ? s.paddingLeft : s.paddingRight, a -= i ? s.paddingTop : s.paddingBottom, this.position.x = r, this.position.y = a
    }, d.layoutPosition = function () {
        var t = this.layout.size, e = {}, i = this.layout._getOption("originLeft"), n = this.layout._getOption("originTop"), o = i ? "paddingLeft" : "paddingRight", s = i ? "left" : "right", r = i ? "right" : "left", a = this.position.x + t[o];
        e[s] = this.getXValue(a), e[r] = "";
        var u = n ? "paddingTop" : "paddingBottom", h = n ? "top" : "bottom", d = n ? "bottom" : "top", l = this.position.y + t[u];
        e[h] = this.getYValue(l), e[d] = "", this.css(e), this.emitEvent("layout", [this])
    }, d.getXValue = function (t) {
        var e = this.layout._getOption("horizontal");
        return this.layout.options.percentPosition && !e ? t / this.layout.size.width * 100 + "%" : t + "px"
    }, d.getYValue = function (t) {
        var e = this.layout._getOption("horizontal");
        return this.layout.options.percentPosition && e ? t / this.layout.size.height * 100 + "%" : t + "px"
    }, d._transitionTo = function (t, e) {
        this.getPosition();
        var i = this.position.x, n = this.position.y, o = parseInt(t, 10), s = parseInt(e, 10), r = o === this.position.x && s === this.position.y;
        if (this.setPosition(t, e), r && !this.isTransitioning)
            return void this.layoutPosition();
        var a = t - i, u = e - n, h = {};
        h.transform = this.getTranslate(a, u), this.transition({to: h, onTransitionEnd: {transform: this.layoutPosition}, isCleaning: !0})
    }, d.getTranslate = function (t, e) {
        var i = this.layout._getOption("originLeft"), n = this.layout._getOption("originTop");
        return t = i ? t : -t, e = n ? e : -e, "translate3d(" + t + "px, " + e + "px, 0)"
    }, d.goTo = function (t, e) {
        this.setPosition(t, e), this.layoutPosition()
    }, d.moveTo = d._transitionTo, d.setPosition = function (t, e) {
        this.position.x = parseInt(t, 10), this.position.y = parseInt(e, 10)
    }, d._nonTransition = function (t) {
        this.css(t.to), t.isCleaning && this._removeStyles(t.to);
        for (var e in t.onTransitionEnd)
            t.onTransitionEnd[e].call(this)
    }, d.transition = function (t) {
        if (!parseFloat(this.layout.options.transitionDuration))
            return void this._nonTransition(t);
        var e = this._transn;
        for (var i in t.onTransitionEnd)
            e.onEnd[i] = t.onTransitionEnd[i];
        for (i in t.to)
            e.ingProperties[i] = !0, t.isCleaning && (e.clean[i] = !0);
        if (t.from) {
            this.css(t.from);
            var n = this.element.offsetHeight;
            n = null
        }
        this.enableTransition(t.to), this.css(t.to), this.isTransitioning = !0
    };
    var l = "opacity," + o(a);
    d.enableTransition = function () {
        if (!this.isTransitioning) {
            var t = this.layout.options.transitionDuration;
            t = "number" == typeof t ? t + "ms" : t, this.css({transitionProperty: l, transitionDuration: t, transitionDelay: this.staggerDelay || 0}), this.element.addEventListener(u, this, !1)
        }
    }, d.onwebkitTransitionEnd = function (t) {
        this.ontransitionend(t)
    }, d.onotransitionend = function (t) {
        this.ontransitionend(t)
    };
    var f = {"-webkit-transform": "transform"};
    d.ontransitionend = function (t) {
        if (t.target === this.element) {
            var e = this._transn, n = f[t.propertyName] || t.propertyName;
            if (delete e.ingProperties[n], i(e.ingProperties) && this.disableTransition(), n in e.clean && (this.element.style[t.propertyName] = "", delete e.clean[n]), n in e.onEnd) {
                var o = e.onEnd[n];
                o.call(this), delete e.onEnd[n]
            }
            this.emitEvent("transitionEnd", [this])
        }
    }, d.disableTransition = function () {
        this.removeTransitionStyles(), this.element.removeEventListener(u, this, !1), this.isTransitioning = !1
    }, d._removeStyles = function (t) {
        var e = {};
        for (var i in t)
            e[i] = "";
        this.css(e)
    };
    var c = {transitionProperty: "", transitionDuration: "", transitionDelay: ""};
    return d.removeTransitionStyles = function () {
        this.css(c)
    }, d.stagger = function (t) {
        t = isNaN(t) ? 0 : t, this.staggerDelay = t + "ms"
    }, d.removeElem = function () {
        this.element.parentNode.removeChild(this.element), this.css({display: ""}), this.emitEvent("remove", [this])
    }, d.remove = function () {
        return r && parseFloat(this.layout.options.transitionDuration) ? (this.once("transitionEnd", function () {
            this.removeElem()
        }), void this.hide()) : void this.removeElem()
    }, d.reveal = function () {
        delete this.isHidden, this.css({display: ""});
        var t = this.layout.options, e = {}, i = this.getHideRevealTransitionEndProperty("visibleStyle");
        e[i] = this.onRevealTransitionEnd, this.transition({from: t.hiddenStyle, to: t.visibleStyle, isCleaning: !0, onTransitionEnd: e})
    }, d.onRevealTransitionEnd = function () {
        this.isHidden || this.emitEvent("reveal")
    }, d.getHideRevealTransitionEndProperty = function (t) {
        var e = this.layout.options[t];
        if (e.opacity)
            return"opacity";
        for (var i in e)
            return i
    }, d.hide = function () {
        this.isHidden = !0, this.css({display: ""});
        var t = this.layout.options, e = {}, i = this.getHideRevealTransitionEndProperty("hiddenStyle");
        e[i] = this.onHideTransitionEnd, this.transition({from: t.visibleStyle, to: t.hiddenStyle, isCleaning: !0, onTransitionEnd: e})
    }, d.onHideTransitionEnd = function () {
        this.isHidden && (this.css({display: "none"}), this.emitEvent("hide"))
    }, d.destroy = function () {
        this.css({position: "", left: "", right: "", top: "", bottom: "", transition: "", transform: ""})
    }, n
}), function (t, e) {
    "use strict";
    "function" == typeof define && define.amd ? define("outlayer/outlayer", ["ev-emitter/ev-emitter", "get-size/get-size", "fizzy-ui-utils/utils", "./item"], function (i, n, o, s) {
        return e(t, i, n, o, s)
    }) : "object" == typeof module && module.exports ? module.exports = e(t, require("ev-emitter"), require("get-size"), require("fizzy-ui-utils"), require("./item")) : t.Outlayer = e(t, t.EvEmitter, t.getSize, t.fizzyUIUtils, t.Outlayer.Item)
}(window, function (t, e, i, n, o) {
    "use strict";
    function s(t, e) {
        var i = n.getQueryElement(t);
        if (!i)
            return void(u && u.error("Bad element for " + this.constructor.namespace + ": " + (i || t)));
        this.element = i, h && (this.$element = h(this.element)), this.options = n.extend({}, this.constructor.defaults), this.option(e);
        var o = ++l;
        this.element.outlayerGUID = o, f[o] = this, this._create();
        var s = this._getOption("initLayout");
        s && this.layout()
    }
    function r(t) {
        function e() {
            t.apply(this, arguments)
        }
        return e.prototype = Object.create(t.prototype), e.prototype.constructor = e, e
    }
    function a(t) {
        if ("number" == typeof t)
            return t;
        var e = t.match(/(^\d*\.?\d*)(\w*)/), i = e && e[1], n = e && e[2];
        if (!i.length)
            return 0;
        i = parseFloat(i);
        var o = m[n] || 1;
        return i * o
    }
    var u = t.console, h = t.jQuery, d = function () {}, l = 0, f = {};
    s.namespace = "outlayer", s.Item = o, s.defaults = {containerStyle: {position: "relative"}, initLayout: !0, originLeft: !0, originTop: !0, resize: !0, resizeContainer: !0, transitionDuration: "0.4s", hiddenStyle: {opacity: 0, transform: "scale(0.001)"}, visibleStyle: {opacity: 1, transform: "scale(1)"}};
    var c = s.prototype;
    n.extend(c, e.prototype), c.option = function (t) {
        n.extend(this.options, t)
    }, c._getOption = function (t) {
        var e = this.constructor.compatOptions[t];
        return e && void 0 !== this.options[e] ? this.options[e] : this.options[t]
    }, s.compatOptions = {initLayout: "isInitLayout", horizontal: "isHorizontal", layoutInstant: "isLayoutInstant", originLeft: "isOriginLeft", originTop: "isOriginTop", resize: "isResizeBound", resizeContainer: "isResizingContainer"}, c._create = function () {
        this.reloadItems(), this.stamps = [], this.stamp(this.options.stamp), n.extend(this.element.style, this.options.containerStyle);
        var t = this._getOption("resize");
        t && this.bindResize()
    }, c.reloadItems = function () {
        this.items = this._itemize(this.element.children)
    }, c._itemize = function (t) {
        for (var e = this._filterFindItemElements(t), i = this.constructor.Item, n = [], o = 0; o < e.length; o++) {
            var s = e[o], r = new i(s, this);
            n.push(r)
        }
        return n
    }, c._filterFindItemElements = function (t) {
        return n.filterFindElements(t, this.options.itemSelector)
    }, c.getItemElements = function () {
        return this.items.map(function (t) {
            return t.element
        })
    }, c.layout = function () {
        this._resetLayout(), this._manageStamps();
        var t = this._getOption("layoutInstant"), e = void 0 !== t ? t : !this._isLayoutInited;
        this.layoutItems(this.items, e), this._isLayoutInited = !0
    }, c._init = c.layout, c._resetLayout = function () {
        this.getSize()
    }, c.getSize = function () {
        this.size = i(this.element)
    }, c._getMeasurement = function (t, e) {
        var n, o = this.options[t];
        o ? ("string" == typeof o ? n = this.element.querySelector(o) : o instanceof HTMLElement && (n = o), this[t] = n ? i(n)[e] : o) : this[t] = 0
    }, c.layoutItems = function (t, e) {
        t = this._getItemsForLayout(t), this._layoutItems(t, e), this._postLayout()
    }, c._getItemsForLayout = function (t) {
        return t.filter(function (t) {
            return!t.isIgnored
        })
    }, c._layoutItems = function (t, e) {
        if (this._emitCompleteOnItems("layout", t), t && t.length) {
            var i = [];
            t.forEach(function (t) {
                var n = this._getItemLayoutPosition(t);
                n.item = t, n.isInstant = e || t.isLayoutInstant, i.push(n)
            }, this), this._processLayoutQueue(i)
        }
    }, c._getItemLayoutPosition = function () {
        return{x: 0, y: 0}
    }, c._processLayoutQueue = function (t) {
        this.updateStagger(), t.forEach(function (t, e) {
            this._positionItem(t.item, t.x, t.y, t.isInstant, e)
        }, this)
    }, c.updateStagger = function () {
        var t = this.options.stagger;
        return null === t || void 0 === t ? void(this.stagger = 0) : (this.stagger = a(t), this.stagger)
    }, c._positionItem = function (t, e, i, n, o) {
        n ? t.goTo(e, i) : (t.stagger(o * this.stagger), t.moveTo(e, i))
    }, c._postLayout = function () {
        this.resizeContainer()
    }, c.resizeContainer = function () {
        var t = this._getOption("resizeContainer");
        if (t) {
            var e = this._getContainerSize();
            e && (this._setContainerMeasure(e.width, !0), this._setContainerMeasure(e.height, !1))
        }
    }, c._getContainerSize = d, c._setContainerMeasure = function (t, e) {
        if (void 0 !== t) {
            var i = this.size;
            i.isBorderBox && (t += e ? i.paddingLeft + i.paddingRight + i.borderLeftWidth + i.borderRightWidth : i.paddingBottom + i.paddingTop + i.borderTopWidth + i.borderBottomWidth), t = Math.max(t, 0), this.element.style[e ? "width" : "height"] = t + "px"
        }
    }, c._emitCompleteOnItems = function (t, e) {
        function i() {
            o.dispatchEvent(t + "Complete", null, [e])
        }
        function n() {
            r++, r == s && i()
        }
        var o = this, s = e.length;
        if (!e || !s)
            return void i();
        var r = 0;
        e.forEach(function (e) {
            e.once(t, n)
        })
    }, c.dispatchEvent = function (t, e, i) {
        var n = e ? [e].concat(i) : i;
        if (this.emitEvent(t, n), h)
            if (this.$element = this.$element || h(this.element), e) {
                var o = h.Event(e);
                o.type = t, this.$element.trigger(o, i)
            } else
                this.$element.trigger(t, i)
    }, c.ignore = function (t) {
        var e = this.getItem(t);
        e && (e.isIgnored = !0)
    }, c.unignore = function (t) {
        var e = this.getItem(t);
        e && delete e.isIgnored
    }, c.stamp = function (t) {
        t = this._find(t), t && (this.stamps = this.stamps.concat(t), t.forEach(this.ignore, this))
    }, c.unstamp = function (t) {
        t = this._find(t), t && t.forEach(function (t) {
            n.removeFrom(this.stamps, t), this.unignore(t)
        }, this)
    }, c._find = function (t) {
        return t ? ("string" == typeof t && (t = this.element.querySelectorAll(t)), t = n.makeArray(t)) : void 0
    }, c._manageStamps = function () {
        this.stamps && this.stamps.length && (this._getBoundingRect(), this.stamps.forEach(this._manageStamp, this))
    }, c._getBoundingRect = function () {
        var t = this.element.getBoundingClientRect(), e = this.size;
        this._boundingRect = {left: t.left + e.paddingLeft + e.borderLeftWidth, top: t.top + e.paddingTop + e.borderTopWidth, right: t.right - (e.paddingRight + e.borderRightWidth), bottom: t.bottom - (e.paddingBottom + e.borderBottomWidth)}
    }, c._manageStamp = d, c._getElementOffset = function (t) {
        var e = t.getBoundingClientRect(), n = this._boundingRect, o = i(t), s = {left: e.left - n.left - o.marginLeft, top: e.top - n.top - o.marginTop, right: n.right - e.right - o.marginRight, bottom: n.bottom - e.bottom - o.marginBottom};
        return s
    }, c.handleEvent = n.handleEvent, c.bindResize = function () {
        t.addEventListener("resize", this), this.isResizeBound = !0
    }, c.unbindResize = function () {
        t.removeEventListener("resize", this), this.isResizeBound = !1
    }, c.onresize = function () {
        this.resize()
    }, n.debounceMethod(s, "onresize", 100), c.resize = function () {
        this.isResizeBound && this.needsResizeLayout() && this.layout()
    }, c.needsResizeLayout = function () {
        var t = i(this.element), e = this.size && t;
        return e && t.innerWidth !== this.size.innerWidth
    }, c.addItems = function (t) {
        var e = this._itemize(t);
        return e.length && (this.items = this.items.concat(e)), e
    }, c.appended = function (t) {
        var e = this.addItems(t);
        e.length && (this.layoutItems(e, !0), this.reveal(e))
    }, c.prepended = function (t) {
        var e = this._itemize(t);
        if (e.length) {
            var i = this.items.slice(0);
            this.items = e.concat(i), this._resetLayout(), this._manageStamps(), this.layoutItems(e, !0), this.reveal(e), this.layoutItems(i)
        }
    }, c.reveal = function (t) {
        if (this._emitCompleteOnItems("reveal", t), t && t.length) {
            var e = this.updateStagger();
            t.forEach(function (t, i) {
                t.stagger(i * e), t.reveal()
            })
        }
    }, c.hide = function (t) {
        if (this._emitCompleteOnItems("hide", t), t && t.length) {
            var e = this.updateStagger();
            t.forEach(function (t, i) {
                t.stagger(i * e), t.hide()
            })
        }
    }, c.revealItemElements = function (t) {
        var e = this.getItems(t);
        this.reveal(e)
    }, c.hideItemElements = function (t) {
        var e = this.getItems(t);
        this.hide(e)
    }, c.getItem = function (t) {
        for (var e = 0; e < this.items.length; e++) {
            var i = this.items[e];
            if (i.element == t)
                return i
        }
    }, c.getItems = function (t) {
        t = n.makeArray(t);
        var e = [];
        return t.forEach(function (t) {
            var i = this.getItem(t);
            i && e.push(i)
        }, this), e
    }, c.remove = function (t) {
        var e = this.getItems(t);
        this._emitCompleteOnItems("remove", e), e && e.length && e.forEach(function (t) {
            t.remove(), n.removeFrom(this.items, t)
        }, this)
    }, c.destroy = function () {
        var t = this.element.style;
        t.height = "", t.position = "", t.width = "", this.items.forEach(function (t) {
            t.destroy()
        }), this.unbindResize();
        var e = this.element.outlayerGUID;
        delete f[e], delete this.element.outlayerGUID, h && h.removeData(this.element, this.constructor.namespace)
    }, s.data = function (t) {
        t = n.getQueryElement(t);
        var e = t && t.outlayerGUID;
        return e && f[e]
    }, s.create = function (t, e) {
        var i = r(s);
        return i.defaults = n.extend({}, s.defaults), n.extend(i.defaults, e), i.compatOptions = n.extend({}, s.compatOptions), i.namespace = t, i.data = s.data, i.Item = r(o), n.htmlInit(i, t), h && h.bridget && h.bridget(t, i), i
    };
    var m = {ms: 1, s: 1e3};
    return s.Item = o, s
}), function (t, e) {
    "function" == typeof define && define.amd ? define("isotope/js/item", ["outlayer/outlayer"], e) : "object" == typeof module && module.exports ? module.exports = e(require("outlayer")) : (t.Isotope = t.Isotope || {}, t.Isotope.Item = e(t.Outlayer))
}(window, function (t) {
    "use strict";
    function e() {
        t.Item.apply(this, arguments)
    }
    var i = e.prototype = Object.create(t.Item.prototype), n = i._create;
    i._create = function () {
        this.id = this.layout.itemGUID++, n.call(this), this.sortData = {}
    }, i.updateSortData = function () {
        if (!this.isIgnored) {
            this.sortData.id = this.id, this.sortData["original-order"] = this.id, this.sortData.random = Math.random();
            var t = this.layout.options.getSortData, e = this.layout._sorters;
            for (var i in t) {
                var n = e[i];
                this.sortData[i] = n(this.element, this)
            }
        }
    };
    var o = i.destroy;
    return i.destroy = function () {
        o.apply(this, arguments), this.css({display: ""})
    }, e
}), function (t, e) {
    "function" == typeof define && define.amd ? define("isotope/js/layout-mode", ["get-size/get-size", "outlayer/outlayer"], e) : "object" == typeof module && module.exports ? module.exports = e(require("get-size"), require("outlayer")) : (t.Isotope = t.Isotope || {}, t.Isotope.LayoutMode = e(t.getSize, t.Outlayer))
}(window, function (t, e) {
    "use strict";
    function i(t) {
        this.isotope = t, t && (this.options = t.options[this.namespace], this.element = t.element, this.items = t.filteredItems, this.size = t.size)
    }
    var n = i.prototype, o = ["_resetLayout", "_getItemLayoutPosition", "_manageStamp", "_getContainerSize", "_getElementOffset", "needsResizeLayout", "_getOption"];
    return o.forEach(function (t) {
        n[t] = function () {
            return e.prototype[t].apply(this.isotope, arguments)
        }
    }), n.needsVerticalResizeLayout = function () {
        var e = t(this.isotope.element), i = this.isotope.size && e;
        return i && e.innerHeight != this.isotope.size.innerHeight
    }, n._getMeasurement = function () {
        this.isotope._getMeasurement.apply(this, arguments)
    }, n.getColumnWidth = function () {
        this.getSegmentSize("column", "Width")
    }, n.getRowHeight = function () {
        this.getSegmentSize("row", "Height")
    }, n.getSegmentSize = function (t, e) {
        var i = t + e, n = "outer" + e;
        if (this._getMeasurement(i, n), !this[i]) {
            var o = this.getFirstItemSize();
            this[i] = o && o[n] || this.isotope.size["inner" + e]
        }
    }, n.getFirstItemSize = function () {
        var e = this.isotope.filteredItems[0];
        return e && e.element && t(e.element)
    }, n.layout = function () {
        this.isotope.layout.apply(this.isotope, arguments)
    }, n.getSize = function () {
        this.isotope.getSize(), this.size = this.isotope.size
    }, i.modes = {}, i.create = function (t, e) {
        function o() {
            i.apply(this, arguments)
        }
        return o.prototype = Object.create(n), o.prototype.constructor = o, e && (o.options = e), o.prototype.namespace = t, i.modes[t] = o, o
    }, i
}), function (t, e) {
    "function" == typeof define && define.amd ? define("masonry/masonry", ["outlayer/outlayer", "get-size/get-size"], e) : "object" == typeof module && module.exports ? module.exports = e(require("outlayer"), require("get-size")) : t.Masonry = e(t.Outlayer, t.getSize)
}(window, function (t, e) {
    var i = t.create("masonry");
    return i.compatOptions.fitWidth = "isFitWidth", i.prototype._resetLayout = function () {
        this.getSize(), this._getMeasurement("columnWidth", "outerWidth"), this._getMeasurement("gutter", "outerWidth"), this.measureColumns(), this.colYs = [];
        for (var t = 0; t < this.cols; t++)
            this.colYs.push(0);
        this.maxY = 0
    }, i.prototype.measureColumns = function () {
        if (this.getContainerWidth(), !this.columnWidth) {
            var t = this.items[0], i = t && t.element;
            this.columnWidth = i && e(i).outerWidth || this.containerWidth
        }
        var n = this.columnWidth += this.gutter, o = this.containerWidth + this.gutter, s = o / n, r = n - o % n, a = r && 1 > r ? "round" : "floor";
        s = Math[a](s), this.cols = Math.max(s, 1)
    }, i.prototype.getContainerWidth = function () {
        var t = this._getOption("fitWidth"), i = t ? this.element.parentNode : this.element, n = e(i);
        this.containerWidth = n && n.innerWidth
    }, i.prototype._getItemLayoutPosition = function (t) {
        t.getSize();
        var e = t.size.outerWidth % this.columnWidth, i = e && 1 > e ? "round" : "ceil", n = Math[i](t.size.outerWidth / this.columnWidth);
        n = Math.min(n, this.cols);
        for (var o = this._getColGroup(n), s = Math.min.apply(Math, o), r = o.indexOf(s), a = {x: this.columnWidth * r, y: s}, u = s + t.size.outerHeight, h = this.cols + 1 - o.length, d = 0; h > d; d++)
            this.colYs[r + d] = u;
        return a
    }, i.prototype._getColGroup = function (t) {
        if (2 > t)
            return this.colYs;
        for (var e = [], i = this.cols + 1 - t, n = 0; i > n; n++) {
            var o = this.colYs.slice(n, n + t);
            e[n] = Math.max.apply(Math, o)
        }
        return e
    }, i.prototype._manageStamp = function (t) {
        var i = e(t), n = this._getElementOffset(t), o = this._getOption("originLeft"), s = o ? n.left : n.right, r = s + i.outerWidth, a = Math.floor(s / this.columnWidth);
        a = Math.max(0, a);
        var u = Math.floor(r / this.columnWidth);
        u -= r % this.columnWidth ? 0 : 1, u = Math.min(this.cols - 1, u);
        for (var h = this._getOption("originTop"), d = (h ? n.top : n.bottom) + i.outerHeight, l = a; u >= l; l++)
            this.colYs[l] = Math.max(d, this.colYs[l])
    }, i.prototype._getContainerSize = function () {
        this.maxY = Math.max.apply(Math, this.colYs);
        var t = {height: this.maxY};
        return this._getOption("fitWidth") && (t.width = this._getContainerFitWidth()), t
    }, i.prototype._getContainerFitWidth = function () {
        for (var t = 0, e = this.cols; --e && 0 === this.colYs[e]; )
            t++;
        return(this.cols - t) * this.columnWidth - this.gutter
    }, i.prototype.needsResizeLayout = function () {
        var t = this.containerWidth;
        return this.getContainerWidth(), t != this.containerWidth
    }, i
}), function (t, e) {
    "function" == typeof define && define.amd ? define("isotope/js/layout-modes/masonry", ["../layout-mode", "masonry/masonry"], e) : "object" == typeof module && module.exports ? module.exports = e(require("../layout-mode"), require("masonry-layout")) : e(t.Isotope.LayoutMode, t.Masonry)
}(window, function (t, e) {
    "use strict";
    var i = t.create("masonry"), n = i.prototype, o = {_getElementOffset: !0, layout: !0, _getMeasurement: !0};
    for (var s in e.prototype)
        o[s] || (n[s] = e.prototype[s]);
    var r = n.measureColumns;
    n.measureColumns = function () {
        this.items = this.isotope.filteredItems, r.call(this)
    };
    var a = n._getOption;
    return n._getOption = function (t) {
        return"fitWidth" == t ? void 0 !== this.options.isFitWidth ? this.options.isFitWidth : this.options.fitWidth : a.apply(this.isotope, arguments)
    }, i
}), function (t, e) {
    "function" == typeof define && define.amd ? define("isotope/js/layout-modes/fit-rows", ["../layout-mode"], e) : "object" == typeof exports ? module.exports = e(require("../layout-mode")) : e(t.Isotope.LayoutMode)
}(window, function (t) {
    "use strict";
    var e = t.create("fitRows"), i = e.prototype;
    return i._resetLayout = function () {
        this.x = 0, this.y = 0, this.maxY = 0, this._getMeasurement("gutter", "outerWidth")
    }, i._getItemLayoutPosition = function (t) {
        t.getSize();
        var e = t.size.outerWidth + this.gutter, i = this.isotope.size.innerWidth + this.gutter;
        0 !== this.x && e + this.x > i && (this.x = 0, this.y = this.maxY);
        var n = {x: this.x, y: this.y};
        return this.maxY = Math.max(this.maxY, this.y + t.size.outerHeight), this.x += e, n
    }, i._getContainerSize = function () {
        return{height: this.maxY}
    }, e
}), function (t, e) {
    "function" == typeof define && define.amd ? define("isotope/js/layout-modes/vertical", ["../layout-mode"], e) : "object" == typeof module && module.exports ? module.exports = e(require("../layout-mode")) : e(t.Isotope.LayoutMode)
}(window, function (t) {
    "use strict";
    var e = t.create("vertical", {horizontalAlignment: 0}), i = e.prototype;
    return i._resetLayout = function () {
        this.y = 0
    }, i._getItemLayoutPosition = function (t) {
        t.getSize();
        var e = (this.isotope.size.innerWidth - t.size.outerWidth) * this.options.horizontalAlignment, i = this.y;
        return this.y += t.size.outerHeight, {x: e, y: i}
    }, i._getContainerSize = function () {
        return{height: this.y}
    }, e
}), function (t, e) {
    "function" == typeof define && define.amd ? define(["outlayer/outlayer", "get-size/get-size", "desandro-matches-selector/matches-selector", "fizzy-ui-utils/utils", "isotope/js/item", "isotope/js/layout-mode", "isotope/js/layout-modes/masonry", "isotope/js/layout-modes/fit-rows", "isotope/js/layout-modes/vertical"], function (i, n, o, s, r, a) {
        return e(t, i, n, o, s, r, a)
    }) : "object" == typeof module && module.exports ? module.exports = e(t, require("outlayer"), require("get-size"), require("desandro-matches-selector"), require("fizzy-ui-utils"), require("isotope/js/item"), require("isotope/js/layout-mode"), require("isotope/js/layout-modes/masonry"), require("isotope/js/layout-modes/fit-rows"), require("isotope/js/layout-modes/vertical")) : t.Isotope = e(t, t.Outlayer, t.getSize, t.matchesSelector, t.fizzyUIUtils, t.Isotope.Item, t.Isotope.LayoutMode)
}(window, function (t, e, i, n, o, s, r) {
    function a(t, e) {
        return function (i, n) {
            for (var o = 0; o < t.length; o++) {
                var s = t[o], r = i.sortData[s], a = n.sortData[s];
                if (r > a || a > r) {
                    var u = void 0 !== e[s] ? e[s] : e, h = u ? 1 : -1;
                    return(r > a ? 1 : -1) * h
                }
            }
            return 0
        }
    }
    var u = t.jQuery, h = String.prototype.trim ? function (t) {
        return t.trim()
    } : function (t) {
        return t.replace(/^\s+|\s+$/g, "")
    }, d = e.create("isotope", {layoutMode: "masonry", isJQueryFiltering: !0, sortAscending: !0});
    d.Item = s, d.LayoutMode = r;
    var l = d.prototype;
    l._create = function () {
        this.itemGUID = 0, this._sorters = {}, this._getSorters(), e.prototype._create.call(this), this.modes = {}, this.filteredItems = this.items, this.sortHistory = ["original-order"];
        for (var t in r.modes)
            this._initLayoutMode(t)
    }, l.reloadItems = function () {
        this.itemGUID = 0, e.prototype.reloadItems.call(this)
    }, l._itemize = function () {
        for (var t = e.prototype._itemize.apply(this, arguments), i = 0; i < t.length; i++) {
            var n = t[i];
            n.id = this.itemGUID++
        }
        return this._updateItemsSortData(t), t
    }, l._initLayoutMode = function (t) {
        var e = r.modes[t], i = this.options[t] || {};
        this.options[t] = e.options ? o.extend(e.options, i) : i, this.modes[t] = new e(this)
    }, l.layout = function () {
        return!this._isLayoutInited && this._getOption("initLayout") ? void this.arrange() : void this._layout()
    }, l._layout = function () {
        var t = this._getIsInstant();
        this._resetLayout(), this._manageStamps(), this.layoutItems(this.filteredItems, t), this._isLayoutInited = !0
    }, l.arrange = function (t) {
        this.option(t), this._getIsInstant();
        var e = this._filter(this.items);
        this.filteredItems = e.matches, this._bindArrangeComplete(), this._isInstant ? this._noTransition(this._hideReveal, [e]) : this._hideReveal(e), this._sort(), this._layout()
    }, l._init = l.arrange, l._hideReveal = function (t) {
        this.reveal(t.needReveal), this.hide(t.needHide)
    }, l._getIsInstant = function () {
        var t = this._getOption("layoutInstant"), e = void 0 !== t ? t : !this._isLayoutInited;
        return this._isInstant = e, e
    }, l._bindArrangeComplete = function () {
        function t() {
            e && i && n && o.dispatchEvent("arrangeComplete", null, [o.filteredItems])
        }
        var e, i, n, o = this;
        this.once("layoutComplete", function () {
            e = !0, t()
        }), this.once("hideComplete", function () {
            i = !0, t()
        }), this.once("revealComplete", function () {
            n = !0, t()
        })
    }, l._filter = function (t) {
        var e = this.options.filter;
        e = e || "*";
        for (var i = [], n = [], o = [], s = this._getFilterTest(e), r = 0; r < t.length; r++) {
            var a = t[r];
            if (!a.isIgnored) {
                var u = s(a);
                u && i.push(a), u && a.isHidden ? n.push(a) : u || a.isHidden || o.push(a)
            }
        }
        return{matches: i, needReveal: n, needHide: o}
    }, l._getFilterTest = function (t) {
        return u && this.options.isJQueryFiltering ? function (e) {
            return u(e.element).is(t)
        } : "function" == typeof t ? function (e) {
            return t(e.element)
        } : function (e) {
            return n(e.element, t)
        }
    }, l.updateSortData = function (t) {
        var e;
        t ? (t = o.makeArray(t), e = this.getItems(t)) : e = this.items, this._getSorters(), this._updateItemsSortData(e)
    }, l._getSorters = function () {
        var t = this.options.getSortData;
        for (var e in t) {
            var i = t[e];
            this._sorters[e] = f(i)
        }
    }, l._updateItemsSortData = function (t) {
        for (var e = t && t.length, i = 0; e && e > i; i++) {
            var n = t[i];
            n.updateSortData()
        }
    };
    var f = function () {
        function t(t) {
            if ("string" != typeof t)
                return t;
            var i = h(t).split(" "), n = i[0], o = n.match(/^\[(.+)\]$/), s = o && o[1], r = e(s, n), a = d.sortDataParsers[i[1]];
            return t = a ? function (t) {
                return t && a(r(t))
            } : function (t) {
                return t && r(t)
            }
        }
        function e(t, e) {
            return t ? function (e) {
                return e.getAttribute(t)
            } : function (t) {
                var i = t.querySelector(e);
                return i && i.textContent
            }
        }
        return t
    }();
    d.sortDataParsers = {parseInt: function (t) {
            return parseInt(t, 10)
        }, parseFloat: function (t) {
            return parseFloat(t)
        }}, l._sort = function () {
        var t = this.options.sortBy;
        if (t) {
            var e = [].concat.apply(t, this.sortHistory), i = a(e, this.options.sortAscending);
            this.filteredItems.sort(i), t != this.sortHistory[0] && this.sortHistory.unshift(t)
        }
    }, l._mode = function () {
        var t = this.options.layoutMode, e = this.modes[t];
        if (!e)
            throw new Error("No layout mode: " + t);
        return e.options = this.options[t], e
    }, l._resetLayout = function () {
        e.prototype._resetLayout.call(this), this._mode()._resetLayout()
    }, l._getItemLayoutPosition = function (t) {
        return this._mode()._getItemLayoutPosition(t)
    }, l._manageStamp = function (t) {
        this._mode()._manageStamp(t)
    }, l._getContainerSize = function () {
        return this._mode()._getContainerSize()
    }, l.needsResizeLayout = function () {
        return this._mode().needsResizeLayout()
    }, l.appended = function (t) {
        var e = this.addItems(t);
        if (e.length) {
            var i = this._filterRevealAdded(e);
            this.filteredItems = this.filteredItems.concat(i)
        }
    }, l.prepended = function (t) {
        var e = this._itemize(t);
        if (e.length) {
            this._resetLayout(), this._manageStamps();
            var i = this._filterRevealAdded(e);
            this.layoutItems(this.filteredItems), this.filteredItems = i.concat(this.filteredItems), this.items = e.concat(this.items)
        }
    }, l._filterRevealAdded = function (t) {
        var e = this._filter(t);
        return this.hide(e.needHide), this.reveal(e.matches), this.layoutItems(e.matches, !0), e.matches
    }, l.insert = function (t) {
        var e = this.addItems(t);
        if (e.length) {
            var i, n, o = e.length;
            for (i = 0; o > i; i++)
                n = e[i], this.element.appendChild(n.element);
            var s = this._filter(e).matches;
            for (i = 0; o > i; i++)
                e[i].isLayoutInstant = !0;
            for (this.arrange(), i = 0; o > i; i++)
                delete e[i].isLayoutInstant;
            this.reveal(s)
        }
    };
    var c = l.remove;
    return l.remove = function (t) {
        t = o.makeArray(t);
        var e = this.getItems(t);
        c.call(this, t);
        for (var i = e && e.length, n = 0; i && i > n; n++) {
            var s = e[n];
            o.removeFrom(this.filteredItems, s)
        }
    }, l.shuffle = function () {
        for (var t = 0; t < this.items.length; t++) {
            var e = this.items[t];
            e.sortData.random = Math.random()
        }
        this.options.sortBy = "random", this._sort(), this._layout()
    }, l._noTransition = function (t, e) {
        var i = this.options.transitionDuration;
        this.options.transitionDuration = 0;
        var n = t.apply(this, e);
        return this.options.transitionDuration = i, n
    }, l.getFilteredItemElements = function () {
        return this.filteredItems.map(function (t) {
            return t.element
        })
    }, d
});










/*!
 * fancyBox - jQuery Plugin
 * version: 2.1.5 (Fri, 14 Jun 2013)
 * @requires jQuery v1.6 or later
 *
 * Examples at http://fancyapps.com/fancybox/
 * License: www.fancyapps.com/fancybox/#license
 *
 * Copyright 2012 Janis Skarnelis - janis@fancyapps.com
 *
 */

(function (window, document, $, undefined) {
    "use strict";

    var H = $("html"),
            W = $(window),
            D = $(document),
            F = $.fancybox = function () {
                F.open.apply(this, arguments);
            },
            IE = navigator.userAgent.match(/msie/i),
            didUpdate = null,
            isTouch = document.createTouch !== undefined,
            isQuery = function (obj) {
                return obj && obj.hasOwnProperty && obj instanceof $;
            },
            isString = function (str) {
                return str && $.type(str) === "string";
            },
            isPercentage = function (str) {
                return isString(str) && str.indexOf('%') > 0;
            },
            isScrollable = function (el) {
                return (el && !(el.style.overflow && el.style.overflow === 'hidden') && ((el.clientWidth && el.scrollWidth > el.clientWidth) || (el.clientHeight && el.scrollHeight > el.clientHeight)));
            },
            getScalar = function (orig, dim) {
                var value = parseInt(orig, 10) || 0;

                if (dim && isPercentage(orig)) {
                    value = F.getViewport()[ dim ] / 100 * value;
                }

                return Math.ceil(value);
            },
            getValue = function (value, dim) {
                return getScalar(value, dim) + 'px';
            };

    $.extend(F, {
        // The current version of fancyBox
        version: '2.1.5',

        defaults: {
            padding: 15,
            margin: 20,

            width: 800,
            height: 600,
            minWidth: 100,
            minHeight: 100,
            maxWidth: 9999,
            maxHeight: 9999,
            pixelRatio: 1, // Set to 2 for retina display support

            autoSize: true,
            autoHeight: false,
            autoWidth: false,

            autoResize: true,
            autoCenter: !isTouch,
            fitToView: true,
            aspectRatio: false,
            topRatio: 0.5,
            leftRatio: 0.5,

            scrolling: 'auto', // 'auto', 'yes' or 'no'
            wrapCSS: '',

            arrows: true,
            closeBtn: true,
            closeClick: false,
            nextClick: false,
            mouseWheel: true,
            autoPlay: false,
            playSpeed: 3000,
            preload: 3,
            modal: false,
            loop: true,

            ajax: {
                dataType: 'html',
                headers: {'X-fancyBox': true}
            },
            iframe: {
                scrolling: 'auto',
                preload: true
            },
            swf: {
                wmode: 'transparent',
                allowfullscreen: 'true',
                allowscriptaccess: 'always'
            },

            keys: {
                next: {
                    13: 'left', // enter
                    34: 'up', // page down
                    39: 'left', // right arrow
                    40: 'up'    // down arrow
                },
                prev: {
                    8: 'right', // backspace
                    33: 'down', // page up
                    37: 'right', // left arrow
                    38: 'down'    // up arrow
                },
                close: [27], // escape key
                play: [32], // space - start/stop slideshow
                toggle: [70]  // letter "f" - toggle fullscreen
            },

            direction: {
                next: 'left',
                prev: 'right'
            },

            scrollOutside: true,

            // Override some properties
            index: 0,
            type: null,
            href: null,
            content: null,
            title: null,

            // HTML templates
            tpl: {
                wrap: '<div class="fancybox-wrap" tabIndex="-1"><div class="fancybox-skin"><div class="fancybox-outer"><div class="fancybox-inner"></div></div></div></div>',
                image: '<img class="fancybox-image" src="{href}" alt="" />',
                iframe: '<iframe id="fancybox-frame{rnd}" name="fancybox-frame{rnd}" class="fancybox-iframe" frameborder="0" vspace="0" hspace="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen' + (IE ? ' allowtransparency="true"' : '') + '></iframe>',
                error: '<p class="fancybox-error">The requested content cannot be loaded.<br/>Please try again later.</p>',
                closeBtn: '<a title="Close" class="fancybox-item fancybox-close" href="javascript:;"></a>',
                next: '<a title="Next" class="fancybox-nav fancybox-next" href="javascript:;"><span></span></a>',
                prev: '<a title="Previous" class="fancybox-nav fancybox-prev" href="javascript:;"><span></span></a>'
            },

            // Properties for each animation type
            // Opening fancyBox
            openEffect: 'fade', // 'elastic', 'fade' or 'none'
            openSpeed: 250,
            openEasing: 'swing',
            openOpacity: true,
            openMethod: 'zoomIn',

            // Closing fancyBox
            closeEffect: 'fade', // 'elastic', 'fade' or 'none'
            closeSpeed: 250,
            closeEasing: 'swing',
            closeOpacity: true,
            closeMethod: 'zoomOut',

            // Changing next gallery item
            nextEffect: 'elastic', // 'elastic', 'fade' or 'none'
            nextSpeed: 250,
            nextEasing: 'swing',
            nextMethod: 'changeIn',

            // Changing previous gallery item
            prevEffect: 'elastic', // 'elastic', 'fade' or 'none'
            prevSpeed: 250,
            prevEasing: 'swing',
            prevMethod: 'changeOut',

            // Enable default helpers
            helpers: {
                overlay: true,
                title: true
            },

            // Callbacks
            onCancel: $.noop, // If canceling
            beforeLoad: $.noop, // Before loading
            afterLoad: $.noop, // After loading
            beforeShow: $.noop, // Before changing in current item
            afterShow: $.noop, // After opening
            beforeChange: $.noop, // Before changing gallery item
            beforeClose: $.noop, // Before closing
            afterClose: $.noop  // After closing
        },

        //Current state
        group: {}, // Selected group
        opts: {}, // Group options
        previous: null, // Previous element
        coming: null, // Element being loaded
        current: null, // Currently loaded element
        isActive: false, // Is activated
        isOpen: false, // Is currently open
        isOpened: false, // Have been fully opened at least once

        wrap: null,
        skin: null,
        outer: null,
        inner: null,

        player: {
            timer: null,
            isActive: false
        },

        // Loaders
        ajaxLoad: null,
        imgPreload: null,

        // Some collections
        transitions: {},
        helpers: {},

        /*
         *  Static methods
         */

        open: function (group, opts) {
            if (!group) {
                return;
            }

            if (!$.isPlainObject(opts)) {
                opts = {};
            }

            // Close if already active
            if (false === F.close(true)) {
                return;
            }

            // Normalize group
            if (!$.isArray(group)) {
                group = isQuery(group) ? $(group).get() : [group];
            }

            // Recheck if the type of each element is `object` and set content type (image, ajax, etc)
            $.each(group, function (i, element) {
                var obj = {},
                        href,
                        title,
                        content,
                        type,
                        rez,
                        hrefParts,
                        selector;

                if ($.type(element) === "object") {
                    // Check if is DOM element
                    if (element.nodeType) {
                        element = $(element);
                    }

                    if (isQuery(element)) {
                        obj = {
                            href: element.data('fancybox-href') || element.attr('href'),
                            title: element.data('fancybox-title') || element.attr('title'),
                            isDom: true,
                            element: element
                        };

                        if ($.metadata) {
                            $.extend(true, obj, element.metadata());
                        }

                    } else {
                        obj = element;
                    }
                }

                href = opts.href || obj.href || (isString(element) ? element : null);
                title = opts.title !== undefined ? opts.title : obj.title || '';

                content = opts.content || obj.content;
                type = content ? 'html' : (opts.type || obj.type);

                if (!type && obj.isDom) {
                    type = element.data('fancybox-type');

                    if (!type) {
                        rez = element.prop('class').match(/fancybox\.(\w+)/);
                        type = rez ? rez[1] : null;
                    }
                }

                if (isString(href)) {
                    // Try to guess the content type
                    if (!type) {
                        if (F.isImage(href)) {
                            type = 'image';

                        } else if (F.isSWF(href)) {
                            type = 'swf';

                        } else if (href.charAt(0) === '#') {
                            type = 'inline';

                        } else if (isString(element)) {
                            type = 'html';
                            content = element;
                        }
                    }

                    // Split url into two pieces with source url and content selector, e.g,
                    // "/mypage.html #my_id" will load "/mypage.html" and display element having id "my_id"
                    if (type === 'ajax') {
                        hrefParts = href.split(/\s+/, 2);
                        href = hrefParts.shift();
                        selector = hrefParts.shift();
                    }
                }

                if (!content) {
                    if (type === 'inline') {
                        if (href) {
                            content = $(isString(href) ? href.replace(/.*(?=#[^\s]+$)/, '') : href); //strip for ie7

                        } else if (obj.isDom) {
                            content = element;
                        }

                    } else if (type === 'html') {
                        content = href;

                    } else if (!type && !href && obj.isDom) {
                        type = 'inline';
                        content = element;
                    }
                }

                $.extend(obj, {
                    href: href,
                    type: type,
                    content: content,
                    title: title,
                    selector: selector
                });

                group[ i ] = obj;
            });

            // Extend the defaults
            F.opts = $.extend(true, {}, F.defaults, opts);

            // All options are merged recursive except keys
            if (opts.keys !== undefined) {
                F.opts.keys = opts.keys ? $.extend({}, F.defaults.keys, opts.keys) : false;
            }

            F.group = group;

            return F._start(F.opts.index);
        },

        // Cancel image loading or abort ajax request
        cancel: function () {
            var coming = F.coming;

            if (!coming || false === F.trigger('onCancel')) {
                return;
            }

            F.hideLoading();

            if (F.ajaxLoad) {
                F.ajaxLoad.abort();
            }

            F.ajaxLoad = null;

            if (F.imgPreload) {
                F.imgPreload.onload = F.imgPreload.onerror = null;
            }

            if (coming.wrap) {
                coming.wrap.stop(true, true).trigger('onReset').remove();
            }

            F.coming = null;

            // If the first item has been canceled, then clear everything
            if (!F.current) {
                F._afterZoomOut(coming);
            }
        },

        // Start closing animation if is open; remove immediately if opening/closing
        close: function (event) {
            F.cancel();

            if (false === F.trigger('beforeClose')) {
                return;
            }

            F.unbindEvents();

            if (!F.isActive) {
                return;
            }

            if (!F.isOpen || event === true) {
                $('.fancybox-wrap').stop(true).trigger('onReset').remove();

                F._afterZoomOut();

            } else {
                F.isOpen = F.isOpened = false;
                F.isClosing = true;

                $('.fancybox-item, .fancybox-nav').remove();

                F.wrap.stop(true, true).removeClass('fancybox-opened');

                F.transitions[ F.current.closeMethod ]();
            }
        },

        // Manage slideshow:
        //   $.fancybox.play(); - toggle slideshow
        //   $.fancybox.play( true ); - start
        //   $.fancybox.play( false ); - stop
        play: function (action) {
            var clear = function () {
                clearTimeout(F.player.timer);
            },
                    set = function () {
                        clear();

                        if (F.current && F.player.isActive) {
                            F.player.timer = setTimeout(F.next, F.current.playSpeed);
                        }
                    },
                    stop = function () {
                        clear();

                        D.unbind('.player');

                        F.player.isActive = false;

                        F.trigger('onPlayEnd');
                    },
                    start = function () {
                        if (F.current && (F.current.loop || F.current.index < F.group.length - 1)) {
                            F.player.isActive = true;

                            D.bind({
                                'onCancel.player beforeClose.player': stop,
                                'onUpdate.player': set,
                                'beforeLoad.player': clear
                            });

                            set();

                            F.trigger('onPlayStart');
                        }
                    };

            if (action === true || (!F.player.isActive && action !== false)) {
                start();
            } else {
                stop();
            }
        },

        // Navigate to next gallery item
        next: function (direction) {
            var current = F.current;

            if (current) {
                if (!isString(direction)) {
                    direction = current.direction.next;
                }

                F.jumpto(current.index + 1, direction, 'next');
            }
        },

        // Navigate to previous gallery item
        prev: function (direction) {
            var current = F.current;

            if (current) {
                if (!isString(direction)) {
                    direction = current.direction.prev;
                }

                F.jumpto(current.index - 1, direction, 'prev');
            }
        },

        // Navigate to gallery item by index
        jumpto: function (index, direction, router) {
            var current = F.current;

            if (!current) {
                return;
            }

            index = getScalar(index);

            F.direction = direction || current.direction[ (index >= current.index ? 'next' : 'prev') ];
            F.router = router || 'jumpto';

            if (current.loop) {
                if (index < 0) {
                    index = current.group.length + (index % current.group.length);
                }

                index = index % current.group.length;
            }

            if (current.group[ index ] !== undefined) {
                F.cancel();

                F._start(index);
            }
        },

        // Center inside viewport and toggle position type to fixed or absolute if needed
        reposition: function (e, onlyAbsolute) {
            var current = F.current,
                    wrap = current ? current.wrap : null,
                    pos;

            if (wrap) {
                pos = F._getPosition(onlyAbsolute);

                if (e && e.type === 'scroll') {
                    delete pos.position;

                    wrap.stop(true, true).animate(pos, 200);

                } else {
                    wrap.css(pos);

                    current.pos = $.extend({}, current.dim, pos);
                }
            }
        },

        update: function (e) {
            var type = (e && e.type),
                    anyway = !type || type === 'orientationchange';

            if (anyway) {
                clearTimeout(didUpdate);

                didUpdate = null;
            }

            if (!F.isOpen || didUpdate) {
                return;
            }

            didUpdate = setTimeout(function () {
                var current = F.current;

                if (!current || F.isClosing) {
                    return;
                }

                F.wrap.removeClass('fancybox-tmp');

                if (anyway || type === 'load' || (type === 'resize' && current.autoResize)) {
                    F._setDimension();
                }

                if (!(type === 'scroll' && current.canShrink)) {
                    F.reposition(e);
                }

                F.trigger('onUpdate');

                didUpdate = null;

            }, (anyway && !isTouch ? 0 : 300));
        },

        // Shrink content to fit inside viewport or restore if resized
        toggle: function (action) {
            if (F.isOpen) {
                F.current.fitToView = $.type(action) === "boolean" ? action : !F.current.fitToView;

                // Help browser to restore document dimensions
                if (isTouch) {
                    F.wrap.removeAttr('style').addClass('fancybox-tmp');

                    F.trigger('onUpdate');
                }

                F.update();
            }
        },

        hideLoading: function () {
            D.unbind('.loading');

            $('#fancybox-loading').remove();
        },

        showLoading: function () {
            var el, viewport;

            F.hideLoading();

            el = $('<div id="fancybox-loading"><div></div></div>').click(F.cancel).appendTo('body');

            // If user will press the escape-button, the request will be canceled
            D.bind('keydown.loading', function (e) {
                if ((e.which || e.keyCode) === 27) {
                    e.preventDefault();

                    F.cancel();
                }
            });

            if (!F.defaults.fixed) {
                viewport = F.getViewport();

                el.css({
                    position: 'absolute',
                    top: (viewport.h * 0.5) + viewport.y,
                    left: (viewport.w * 0.5) + viewport.x
                });
            }
        },

        getViewport: function () {
            var locked = (F.current && F.current.locked) || false,
                    rez = {
                        x: W.scrollLeft(),
                        y: W.scrollTop()
                    };

            if (locked) {
                rez.w = locked[0].clientWidth;
                rez.h = locked[0].clientHeight;

            } else {
                // See http://bugs.jquery.com/ticket/6724
                rez.w = isTouch && window.innerWidth ? window.innerWidth : W.width();
                rez.h = isTouch && window.innerHeight ? window.innerHeight : W.height();
            }

            return rez;
        },

        // Unbind the keyboard / clicking actions
        unbindEvents: function () {
            if (F.wrap && isQuery(F.wrap)) {
                F.wrap.unbind('.fb');
            }

            D.unbind('.fb');
            W.unbind('.fb');
        },

        bindEvents: function () {
            var current = F.current,
                    keys;

            if (!current) {
                return;
            }

            // Changing document height on iOS devices triggers a 'resize' event,
            // that can change document height... repeating infinitely
            W.bind('orientationchange.fb' + (isTouch ? '' : ' resize.fb') + (current.autoCenter && !current.locked ? ' scroll.fb' : ''), F.update);

            keys = current.keys;

            if (keys) {
                D.bind('keydown.fb', function (e) {
                    var code = e.which || e.keyCode,
                            target = e.target || e.srcElement;

                    // Skip esc key if loading, because showLoading will cancel preloading
                    if (code === 27 && F.coming) {
                        return false;
                    }

                    // Ignore key combinations and key events within form elements
                    if (!e.ctrlKey && !e.altKey && !e.shiftKey && !e.metaKey && !(target && (target.type || $(target).is('[contenteditable]')))) {
                        $.each(keys, function (i, val) {
                            if (current.group.length > 1 && val[ code ] !== undefined) {
                                F[ i ](val[ code ]);

                                e.preventDefault();
                                return false;
                            }

                            if ($.inArray(code, val) > -1) {
                                F[ i ]();

                                e.preventDefault();
                                return false;
                            }
                        });
                    }
                });
            }

            if ($.fn.mousewheel && current.mouseWheel) {
                F.wrap.bind('mousewheel.fb', function (e, delta, deltaX, deltaY) {
                    var target = e.target || null,
                            parent = $(target),
                            canScroll = false;

                    while (parent.length) {
                        if (canScroll || parent.is('.fancybox-skin') || parent.is('.fancybox-wrap')) {
                            break;
                        }

                        canScroll = isScrollable(parent[0]);
                        parent = $(parent).parent();
                    }

                    if (delta !== 0 && !canScroll) {
                        if (F.group.length > 1 && !current.canShrink) {
                            if (deltaY > 0 || deltaX > 0) {
                                F.prev(deltaY > 0 ? 'down' : 'left');

                            } else if (deltaY < 0 || deltaX < 0) {
                                F.next(deltaY < 0 ? 'up' : 'right');
                            }

                            e.preventDefault();
                        }
                    }
                });
            }
        },

        trigger: function (event, o) {
            var ret, obj = o || F.coming || F.current;

            if (!obj) {
                return;
            }

            if ($.isFunction(obj[event])) {
                ret = obj[event].apply(obj, Array.prototype.slice.call(arguments, 1));
            }

            if (ret === false) {
                return false;
            }

            if (obj.helpers) {
                $.each(obj.helpers, function (helper, opts) {
                    if (opts && F.helpers[helper] && $.isFunction(F.helpers[helper][event])) {
                        F.helpers[helper][event]($.extend(true, {}, F.helpers[helper].defaults, opts), obj);
                    }
                });
            }

            D.trigger(event);
        },

        isImage: function (str) {
            return isString(str) && str.match(/(^data:image\/.*,)|(\.(jp(e|g|eg)|gif|png|bmp|webp|svg)((\?|#).*)?$)/i);
        },

        isSWF: function (str) {
            return isString(str) && str.match(/\.(swf)((\?|#).*)?$/i);
        },

        _start: function (index) {
            var coming = {},
                    obj,
                    href,
                    type,
                    margin,
                    padding;

            index = getScalar(index);
            obj = F.group[ index ] || null;

            if (!obj) {
                return false;
            }

            coming = $.extend(true, {}, F.opts, obj);

            // Convert margin and padding properties to array - top, right, bottom, left
            margin = coming.margin;
            padding = coming.padding;

            if ($.type(margin) === 'number') {
                coming.margin = [margin, margin, margin, margin];
            }

            if ($.type(padding) === 'number') {
                coming.padding = [padding, padding, padding, padding];
            }

            // 'modal' propery is just a shortcut
            if (coming.modal) {
                $.extend(true, coming, {
                    closeBtn: false,
                    closeClick: false,
                    nextClick: false,
                    arrows: false,
                    mouseWheel: false,
                    keys: null,
                    helpers: {
                        overlay: {
                            closeClick: false
                        }
                    }
                });
            }

            // 'autoSize' property is a shortcut, too
            if (coming.autoSize) {
                coming.autoWidth = coming.autoHeight = true;
            }

            if (coming.width === 'auto') {
                coming.autoWidth = true;
            }

            if (coming.height === 'auto') {
                coming.autoHeight = true;
            }

            /*
             * Add reference to the group, so it`s possible to access from callbacks, example:
             * afterLoad : function() {
             *     this.title = 'Image ' + (this.index + 1) + ' of ' + this.group.length + (this.title ? ' - ' + this.title : '');
             * }
             */

            coming.group = F.group;
            coming.index = index;

            // Give a chance for callback or helpers to update coming item (type, title, etc)
            F.coming = coming;

            if (false === F.trigger('beforeLoad')) {
                F.coming = null;

                return;
            }

            type = coming.type;
            href = coming.href;

            if (!type) {
                F.coming = null;

                //If we can not determine content type then drop silently or display next/prev item if looping through gallery
                if (F.current && F.router && F.router !== 'jumpto') {
                    F.current.index = index;

                    return F[ F.router ](F.direction);
                }

                return false;
            }

            F.isActive = true;

            if (type === 'image' || type === 'swf') {
                coming.autoHeight = coming.autoWidth = false;
                coming.scrolling = 'visible';
            }

            if (type === 'image') {
                coming.aspectRatio = true;
            }

            if (type === 'iframe' && isTouch) {
                coming.scrolling = 'scroll';
            }

            // Build the neccessary markup
            coming.wrap = $(coming.tpl.wrap).addClass('fancybox-' + (isTouch ? 'mobile' : 'desktop') + ' fancybox-type-' + type + ' fancybox-tmp ' + coming.wrapCSS).appendTo(coming.parent || 'body');

            $.extend(coming, {
                skin: $('.fancybox-skin', coming.wrap),
                outer: $('.fancybox-outer', coming.wrap),
                inner: $('.fancybox-inner', coming.wrap)
            });

            $.each(["Top", "Right", "Bottom", "Left"], function (i, v) {
                coming.skin.css('padding' + v, getValue(coming.padding[ i ]));
            });

            F.trigger('onReady');

            // Check before try to load; 'inline' and 'html' types need content, others - href
            if (type === 'inline' || type === 'html') {
                if (!coming.content || !coming.content.length) {
                    return F._error('content');
                }

            } else if (!href) {
                return F._error('href');
            }

            if (type === 'image') {
                F._loadImage();

            } else if (type === 'ajax') {
                F._loadAjax();

            } else if (type === 'iframe') {
                F._loadIframe();

            } else {
                F._afterLoad();
            }
        },

        _error: function (type) {
            $.extend(F.coming, {
                type: 'html',
                autoWidth: true,
                autoHeight: true,
                minWidth: 0,
                minHeight: 0,
                scrolling: 'no',
                hasError: type,
                content: F.coming.tpl.error
            });

            F._afterLoad();
        },

        _loadImage: function () {
            // Reset preload image so it is later possible to check "complete" property
            var img = F.imgPreload = new Image();

            img.onload = function () {
                this.onload = this.onerror = null;

                F.coming.width = this.width / F.opts.pixelRatio;
                F.coming.height = this.height / F.opts.pixelRatio;

                F._afterLoad();
            };

            img.onerror = function () {
                this.onload = this.onerror = null;

                F._error('image');
            };

            img.src = F.coming.href;

            if (img.complete !== true) {
                F.showLoading();
            }
        },

        _loadAjax: function () {
            var coming = F.coming;

            F.showLoading();

            F.ajaxLoad = $.ajax($.extend({}, coming.ajax, {
                url: coming.href,
                error: function (jqXHR, textStatus) {
                    if (F.coming && textStatus !== 'abort') {
                        F._error('ajax', jqXHR);

                    } else {
                        F.hideLoading();
                    }
                },
                success: function (data, textStatus) {
                    if (textStatus === 'success') {
                        coming.content = data;

                        F._afterLoad();
                    }
                }
            }));
        },

        _loadIframe: function () {
            var coming = F.coming,
                    iframe = $(coming.tpl.iframe.replace(/\{rnd\}/g, new Date().getTime()))
                    .attr('scrolling', isTouch ? 'auto' : coming.iframe.scrolling)
                    .attr('src', coming.href);

            // This helps IE
            $(coming.wrap).bind('onReset', function () {
                try {
                    $(this).find('iframe').hide().attr('src', '//about:blank').end().empty();
                } catch (e) {
                }
            });

            if (coming.iframe.preload) {
                F.showLoading();

                iframe.one('load', function () {
                    $(this).data('ready', 1);

                    // iOS will lose scrolling if we resize
                    if (!isTouch) {
                        $(this).bind('load.fb', F.update);
                    }

                    // Without this trick:
                    //   - iframe won't scroll on iOS devices
                    //   - IE7 sometimes displays empty iframe
                    $(this).parents('.fancybox-wrap').width('100%').removeClass('fancybox-tmp').show();

                    F._afterLoad();
                });
            }

            coming.content = iframe.appendTo(coming.inner);

            if (!coming.iframe.preload) {
                F._afterLoad();
            }
        },

        _preloadImages: function () {
            var group = F.group,
                    current = F.current,
                    len = group.length,
                    cnt = current.preload ? Math.min(current.preload, len - 1) : 0,
                    item,
                    i;

            for (i = 1; i <= cnt; i += 1) {
                item = group[ (current.index + i) % len ];

                if (item.type === 'image' && item.href) {
                    new Image().src = item.href;
                }
            }
        },

        _afterLoad: function () {
            var coming = F.coming,
                    previous = F.current,
                    placeholder = 'fancybox-placeholder',
                    current,
                    content,
                    type,
                    scrolling,
                    href,
                    embed;

            F.hideLoading();

            if (!coming || F.isActive === false) {
                return;
            }

            if (false === F.trigger('afterLoad', coming, previous)) {
                coming.wrap.stop(true).trigger('onReset').remove();

                F.coming = null;

                return;
            }

            if (previous) {
                F.trigger('beforeChange', previous);

                previous.wrap.stop(true).removeClass('fancybox-opened')
                        .find('.fancybox-item, .fancybox-nav')
                        .remove();
            }

            F.unbindEvents();

            current = coming;
            content = coming.content;
            type = coming.type;
            scrolling = coming.scrolling;

            $.extend(F, {
                wrap: current.wrap,
                skin: current.skin,
                outer: current.outer,
                inner: current.inner,
                current: current,
                previous: previous
            });

            href = current.href;

            switch (type) {
                case 'inline':
                case 'ajax':
                case 'html':
                    if (current.selector) {
                        content = $('<div>').html(content).find(current.selector);

                    } else if (isQuery(content)) {
                        if (!content.data(placeholder)) {
                            content.data(placeholder, $('<div class="' + placeholder + '"></div>').insertAfter(content).hide());
                        }

                        content = content.show().detach();

                        current.wrap.bind('onReset', function () {
                            if ($(this).find(content).length) {
                                content.hide().replaceAll(content.data(placeholder)).data(placeholder, false);
                            }
                        });
                    }
                    break;

                case 'image':
                    content = current.tpl.image.replace('{href}', href);
                    break;

                case 'swf':
                    content = '<object id="fancybox-swf" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%"><param name="movie" value="' + href + '"></param>';
                    embed = '';

                    $.each(current.swf, function (name, val) {
                        content += '<param name="' + name + '" value="' + val + '"></param>';
                        embed += ' ' + name + '="' + val + '"';
                    });

                    content += '<embed src="' + href + '" type="application/x-shockwave-flash" width="100%" height="100%"' + embed + '></embed></object>';
                    break;
            }

            if (!(isQuery(content) && content.parent().is(current.inner))) {
                current.inner.append(content);
            }

            // Give a chance for helpers or callbacks to update elements
            F.trigger('beforeShow');

            // Set scrolling before calculating dimensions
            current.inner.css('overflow', scrolling === 'yes' ? 'scroll' : (scrolling === 'no' ? 'hidden' : scrolling));

            // Set initial dimensions and start position
            F._setDimension();

            F.reposition();

            F.isOpen = false;
            F.coming = null;

            F.bindEvents();

            if (!F.isOpened) {
                $('.fancybox-wrap').not(current.wrap).stop(true).trigger('onReset').remove();

            } else if (previous.prevMethod) {
                F.transitions[ previous.prevMethod ]();
            }

            F.transitions[ F.isOpened ? current.nextMethod : current.openMethod ]();

            F._preloadImages();
        },

        _setDimension: function () {
            var viewport = F.getViewport(),
                    steps = 0,
                    canShrink = false,
                    canExpand = false,
                    wrap = F.wrap,
                    skin = F.skin,
                    inner = F.inner,
                    current = F.current,
                    width = current.width,
                    height = current.height,
                    minWidth = current.minWidth,
                    minHeight = current.minHeight,
                    maxWidth = current.maxWidth,
                    maxHeight = current.maxHeight,
                    scrolling = current.scrolling,
                    scrollOut = current.scrollOutside ? current.scrollbarWidth : 0,
                    margin = current.margin,
                    wMargin = getScalar(margin[1] + margin[3]),
                    hMargin = getScalar(margin[0] + margin[2]),
                    wPadding,
                    hPadding,
                    wSpace,
                    hSpace,
                    origWidth,
                    origHeight,
                    origMaxWidth,
                    origMaxHeight,
                    ratio,
                    width_,
                    height_,
                    maxWidth_,
                    maxHeight_,
                    iframe,
                    body;

            // Reset dimensions so we could re-check actual size
            wrap.add(skin).add(inner).width('auto').height('auto').removeClass('fancybox-tmp');

            wPadding = getScalar(skin.outerWidth(true) - skin.width());
            hPadding = getScalar(skin.outerHeight(true) - skin.height());

            // Any space between content and viewport (margin, padding, border, title)
            wSpace = wMargin + wPadding;
            hSpace = hMargin + hPadding;

            origWidth = isPercentage(width) ? (viewport.w - wSpace) * getScalar(width) / 100 : width;
            origHeight = isPercentage(height) ? (viewport.h - hSpace) * getScalar(height) / 100 : height;

            if (current.type === 'iframe') {
                iframe = current.content;

                if (current.autoHeight && iframe.data('ready') === 1) {
                    try {
                        if (iframe[0].contentWindow.document.location) {
                            inner.width(origWidth).height(9999);

                            body = iframe.contents().find('body');

                            if (scrollOut) {
                                body.css('overflow-x', 'hidden');
                            }

                            origHeight = body.outerHeight(true);
                        }

                    } catch (e) {
                    }
                }

            } else if (current.autoWidth || current.autoHeight) {
                inner.addClass('fancybox-tmp');

                // Set width or height in case we need to calculate only one dimension
                if (!current.autoWidth) {
                    inner.width(origWidth);
                }

                if (!current.autoHeight) {
                    inner.height(origHeight);
                }

                if (current.autoWidth) {
                    origWidth = inner.width();
                }

                if (current.autoHeight) {
                    origHeight = inner.height();
                }

                inner.removeClass('fancybox-tmp');
            }

            width = getScalar(origWidth);
            height = getScalar(origHeight);

            ratio = origWidth / origHeight;

            // Calculations for the content
            minWidth = getScalar(isPercentage(minWidth) ? getScalar(minWidth, 'w') - wSpace : minWidth);
            maxWidth = getScalar(isPercentage(maxWidth) ? getScalar(maxWidth, 'w') - wSpace : maxWidth);

            minHeight = getScalar(isPercentage(minHeight) ? getScalar(minHeight, 'h') - hSpace : minHeight);
            maxHeight = getScalar(isPercentage(maxHeight) ? getScalar(maxHeight, 'h') - hSpace : maxHeight);

            // These will be used to determine if wrap can fit in the viewport
            origMaxWidth = maxWidth;
            origMaxHeight = maxHeight;

            if (current.fitToView) {
                maxWidth = Math.min(viewport.w - wSpace, maxWidth);
                maxHeight = Math.min(viewport.h - hSpace, maxHeight);
            }

            maxWidth_ = viewport.w - wMargin;
            maxHeight_ = viewport.h - hMargin;

            if (current.aspectRatio) {
                if (width > maxWidth) {
                    width = maxWidth;
                    height = getScalar(width / ratio);
                }

                if (height > maxHeight) {
                    height = maxHeight;
                    width = getScalar(height * ratio);
                }

                if (width < minWidth) {
                    width = minWidth;
                    height = getScalar(width / ratio);
                }

                if (height < minHeight) {
                    height = minHeight;
                    width = getScalar(height * ratio);
                }

            } else {
                width = Math.max(minWidth, Math.min(width, maxWidth));

                if (current.autoHeight && current.type !== 'iframe') {
                    inner.width(width);

                    height = inner.height();
                }

                height = Math.max(minHeight, Math.min(height, maxHeight));
            }

            // Try to fit inside viewport (including the title)
            if (current.fitToView) {
                inner.width(width).height(height);

                wrap.width(width + wPadding);

                // Real wrap dimensions
                width_ = wrap.width();
                height_ = wrap.height();

                if (current.aspectRatio) {
                    while ((width_ > maxWidth_ || height_ > maxHeight_) && width > minWidth && height > minHeight) {
                        if (steps++ > 19) {
                            break;
                        }

                        height = Math.max(minHeight, Math.min(maxHeight, height - 10));
                        width = getScalar(height * ratio);

                        if (width < minWidth) {
                            width = minWidth;
                            height = getScalar(width / ratio);
                        }

                        if (width > maxWidth) {
                            width = maxWidth;
                            height = getScalar(width / ratio);
                        }

                        inner.width(width).height(height);

                        wrap.width(width + wPadding);

                        width_ = wrap.width();
                        height_ = wrap.height();
                    }

                } else {
                    width = Math.max(minWidth, Math.min(width, width - (width_ - maxWidth_)));
                    height = Math.max(minHeight, Math.min(height, height - (height_ - maxHeight_)));
                }
            }

            if (scrollOut && scrolling === 'auto' && height < origHeight && (width + wPadding + scrollOut) < maxWidth_) {
                width += scrollOut;
            }

            inner.width(width).height(height);

            wrap.width(width + wPadding);

            width_ = wrap.width();
            height_ = wrap.height();

            canShrink = (width_ > maxWidth_ || height_ > maxHeight_) && width > minWidth && height > minHeight;
            canExpand = current.aspectRatio ? (width < origMaxWidth && height < origMaxHeight && width < origWidth && height < origHeight) : ((width < origMaxWidth || height < origMaxHeight) && (width < origWidth || height < origHeight));

            $.extend(current, {
                dim: {
                    width: getValue(width_),
                    height: getValue(height_)
                },
                origWidth: origWidth,
                origHeight: origHeight,
                canShrink: canShrink,
                canExpand: canExpand,
                wPadding: wPadding,
                hPadding: hPadding,
                wrapSpace: height_ - skin.outerHeight(true),
                skinSpace: skin.height() - height
            });

            if (!iframe && current.autoHeight && height > minHeight && height < maxHeight && !canExpand) {
                inner.height('auto');
            }
        },

        _getPosition: function (onlyAbsolute) {
            var current = F.current,
                    viewport = F.getViewport(),
                    margin = current.margin,
                    width = F.wrap.width() + margin[1] + margin[3],
                    height = F.wrap.height() + margin[0] + margin[2],
                    rez = {
                        position: 'absolute',
                        top: margin[0],
                        left: margin[3]
                    };

            if (current.autoCenter && current.fixed && !onlyAbsolute && height <= viewport.h && width <= viewport.w) {
                rez.position = 'fixed';

            } else if (!current.locked) {
                rez.top += viewport.y;
                rez.left += viewport.x;
            }

            rez.top = getValue(Math.max(rez.top, rez.top + ((viewport.h - height) * current.topRatio)));
            rez.left = getValue(Math.max(rez.left, rez.left + ((viewport.w - width) * current.leftRatio)));

            return rez;
        },

        _afterZoomIn: function () {
            var current = F.current;

            if (!current) {
                return;
            }

            F.isOpen = F.isOpened = true;

            F.wrap.css('overflow', 'visible').addClass('fancybox-opened');

            F.update();

            // Assign a click event
            if (current.closeClick || (current.nextClick && F.group.length > 1)) {
                F.inner.css('cursor', 'pointer').bind('click.fb', function (e) {
                    if (!$(e.target).is('a') && !$(e.target).parent().is('a')) {
                        e.preventDefault();

                        F[ current.closeClick ? 'close' : 'next' ]();
                    }
                });
            }

            // Create a close button
            if (current.closeBtn) {
                $(current.tpl.closeBtn).appendTo(F.skin).bind('click.fb', function (e) {
                    e.preventDefault();

                    F.close();
                });
            }

            // Create navigation arrows
            if (current.arrows && F.group.length > 1) {
                if (current.loop || current.index > 0) {
                    $(current.tpl.prev).appendTo(F.outer).bind('click.fb', F.prev);
                }

                if (current.loop || current.index < F.group.length - 1) {
                    $(current.tpl.next).appendTo(F.outer).bind('click.fb', F.next);
                }
            }

            F.trigger('afterShow');

            // Stop the slideshow if this is the last item
            if (!current.loop && current.index === current.group.length - 1) {
                F.play(false);

            } else if (F.opts.autoPlay && !F.player.isActive) {
                F.opts.autoPlay = false;

                F.play();
            }
        },

        _afterZoomOut: function (obj) {
            obj = obj || F.current;

            $('.fancybox-wrap').trigger('onReset').remove();

            $.extend(F, {
                group: {},
                opts: {},
                router: false,
                current: null,
                isActive: false,
                isOpened: false,
                isOpen: false,
                isClosing: false,
                wrap: null,
                skin: null,
                outer: null,
                inner: null
            });

            F.trigger('afterClose', obj);
        }
    });

    /*
     *  Default transitions
     */

    F.transitions = {
        getOrigPosition: function () {
            var current = F.current,
                    element = current.element,
                    orig = current.orig,
                    pos = {},
                    width = 50,
                    height = 50,
                    hPadding = current.hPadding,
                    wPadding = current.wPadding,
                    viewport = F.getViewport();

            if (!orig && current.isDom && element.is(':visible')) {
                orig = element.find('img:first');

                if (!orig.length) {
                    orig = element;
                }
            }

            if (isQuery(orig)) {
                pos = orig.offset();

                if (orig.is('img')) {
                    width = orig.outerWidth();
                    height = orig.outerHeight();
                }

            } else {
                pos.top = viewport.y + (viewport.h - height) * current.topRatio;
                pos.left = viewport.x + (viewport.w - width) * current.leftRatio;
            }

            if (F.wrap.css('position') === 'fixed' || current.locked) {
                pos.top -= viewport.y;
                pos.left -= viewport.x;
            }

            pos = {
                top: getValue(pos.top - hPadding * current.topRatio),
                left: getValue(pos.left - wPadding * current.leftRatio),
                width: getValue(width + wPadding),
                height: getValue(height + hPadding)
            };

            return pos;
        },

        step: function (now, fx) {
            var ratio,
                    padding,
                    value,
                    prop = fx.prop,
                    current = F.current,
                    wrapSpace = current.wrapSpace,
                    skinSpace = current.skinSpace;

            if (prop === 'width' || prop === 'height') {
                ratio = fx.end === fx.start ? 1 : (now - fx.start) / (fx.end - fx.start);

                if (F.isClosing) {
                    ratio = 1 - ratio;
                }

                padding = prop === 'width' ? current.wPadding : current.hPadding;
                value = now - padding;

                F.skin[ prop ](getScalar(prop === 'width' ? value : value - (wrapSpace * ratio)));
                F.inner[ prop ](getScalar(prop === 'width' ? value : value - (wrapSpace * ratio) - (skinSpace * ratio)));
            }
        },

        zoomIn: function () {
            var current = F.current,
                    startPos = current.pos,
                    effect = current.openEffect,
                    elastic = effect === 'elastic',
                    endPos = $.extend({opacity: 1}, startPos);

            // Remove "position" property that breaks older IE
            delete endPos.position;

            if (elastic) {
                startPos = this.getOrigPosition();

                if (current.openOpacity) {
                    startPos.opacity = 0.1;
                }

            } else if (effect === 'fade') {
                startPos.opacity = 0.1;
            }

            F.wrap.css(startPos).animate(endPos, {
                duration: effect === 'none' ? 0 : current.openSpeed,
                easing: current.openEasing,
                step: elastic ? this.step : null,
                complete: F._afterZoomIn
            });
        },

        zoomOut: function () {
            var current = F.current,
                    effect = current.closeEffect,
                    elastic = effect === 'elastic',
                    endPos = {opacity: 0.1};

            if (elastic) {
                endPos = this.getOrigPosition();

                if (current.closeOpacity) {
                    endPos.opacity = 0.1;
                }
            }

            F.wrap.animate(endPos, {
                duration: effect === 'none' ? 0 : current.closeSpeed,
                easing: current.closeEasing,
                step: elastic ? this.step : null,
                complete: F._afterZoomOut
            });
        },

        changeIn: function () {
            var current = F.current,
                    effect = current.nextEffect,
                    startPos = current.pos,
                    endPos = {opacity: 1},
                    direction = F.direction,
                    distance = 200,
                    field;

            startPos.opacity = 0.1;

            if (effect === 'elastic') {
                field = direction === 'down' || direction === 'up' ? 'top' : 'left';

                if (direction === 'down' || direction === 'right') {
                    startPos[ field ] = getValue(getScalar(startPos[ field ]) - distance);
                    endPos[ field ] = '+=' + distance + 'px';

                } else {
                    startPos[ field ] = getValue(getScalar(startPos[ field ]) + distance);
                    endPos[ field ] = '-=' + distance + 'px';
                }
            }

            // Workaround for http://bugs.jquery.com/ticket/12273
            if (effect === 'none') {
                F._afterZoomIn();

            } else {
                F.wrap.css(startPos).animate(endPos, {
                    duration: current.nextSpeed,
                    easing: current.nextEasing,
                    complete: F._afterZoomIn
                });
            }
        },

        changeOut: function () {
            var previous = F.previous,
                    effect = previous.prevEffect,
                    endPos = {opacity: 0.1},
                    direction = F.direction,
                    distance = 200;

            if (effect === 'elastic') {
                endPos[ direction === 'down' || direction === 'up' ? 'top' : 'left' ] = (direction === 'up' || direction === 'left' ? '-' : '+') + '=' + distance + 'px';
            }

            previous.wrap.animate(endPos, {
                duration: effect === 'none' ? 0 : previous.prevSpeed,
                easing: previous.prevEasing,
                complete: function () {
                    $(this).trigger('onReset').remove();
                }
            });
        }
    };

    /*
     *  Overlay helper
     */

    F.helpers.overlay = {
        defaults: {
            closeClick: true, // if true, fancyBox will be closed when user clicks on the overlay
            speedOut: 200, // duration of fadeOut animation
            showEarly: true, // indicates if should be opened immediately or wait until the content is ready
            css: {}, // custom CSS properties
            locked: !isTouch, // if true, the content will be locked into overlay
            fixed: true       // if false, the overlay CSS position property will not be set to "fixed"
        },

        overlay: null, // current handle
        fixed: false, // indicates if the overlay has position "fixed"
        el: $('html'), // element that contains "the lock"

        // Public methods
        create: function (opts) {
            opts = $.extend({}, this.defaults, opts);

            if (this.overlay) {
                this.close();
            }

            this.overlay = $('<div class="fancybox-overlay"></div>').appendTo(F.coming ? F.coming.parent : opts.parent);
            this.fixed = false;

            if (opts.fixed && F.defaults.fixed) {
                this.overlay.addClass('fancybox-overlay-fixed');

                this.fixed = true;
            }
        },

        open: function (opts) {
            var that = this;

            opts = $.extend({}, this.defaults, opts);

            if (this.overlay) {
                this.overlay.unbind('.overlay').width('auto').height('auto');

            } else {
                this.create(opts);
            }

            if (!this.fixed) {
                W.bind('resize.overlay', $.proxy(this.update, this));

                this.update();
            }

            if (opts.closeClick) {
                this.overlay.bind('click.overlay', function (e) {
                    if ($(e.target).hasClass('fancybox-overlay')) {
                        if (F.isActive) {
                            F.close();
                        } else {
                            that.close();
                        }

                        return false;
                    }
                });
            }

            this.overlay.css(opts.css).show();
        },

        close: function () {
            var scrollV, scrollH;

            W.unbind('resize.overlay');

            if (this.el.hasClass('fancybox-lock')) {
                $('.fancybox-margin').removeClass('fancybox-margin');

                scrollV = W.scrollTop();
                scrollH = W.scrollLeft();

                this.el.removeClass('fancybox-lock');

                W.scrollTop(scrollV).scrollLeft(scrollH);
            }

            $('.fancybox-overlay').remove().hide();

            $.extend(this, {
                overlay: null,
                fixed: false
            });
        },

        // Private, callbacks

        update: function () {
            var width = '100%', offsetWidth;

            // Reset width/height so it will not mess
            this.overlay.width(width).height('100%');

            // jQuery does not return reliable result for IE
            if (IE) {
                offsetWidth = Math.max(document.documentElement.offsetWidth, document.body.offsetWidth);

                if (D.width() > offsetWidth) {
                    width = D.width();
                }

            } else if (D.width() > W.width()) {
                width = D.width();
            }

            this.overlay.width(width).height(D.height());
        },

        // This is where we can manipulate DOM, because later it would cause iframes to reload
        onReady: function (opts, obj) {
            var overlay = this.overlay;

            $('.fancybox-overlay').stop(true, true);

            if (!overlay) {
                this.create(opts);
            }

            if (opts.locked && this.fixed && obj.fixed) {
                if (!overlay) {
                    this.margin = D.height() > W.height() ? $('html').css('margin-right').replace("px", "") : false;
                }

                obj.locked = this.overlay.append(obj.wrap);
                obj.fixed = false;
            }

            if (opts.showEarly === true) {
                this.beforeShow.apply(this, arguments);
            }
        },

        beforeShow: function (opts, obj) {
            var scrollV, scrollH;

            if (obj.locked) {
                if (this.margin !== false) {
                    $('*').filter(function () {
                        return ($(this).css('position') === 'fixed' && !$(this).hasClass("fancybox-overlay") && !$(this).hasClass("fancybox-wrap"));
                    }).addClass('fancybox-margin');

                    this.el.addClass('fancybox-margin');
                }

                scrollV = W.scrollTop();
                scrollH = W.scrollLeft();

                this.el.addClass('fancybox-lock');

                W.scrollTop(scrollV).scrollLeft(scrollH);
            }

            this.open(opts);
        },

        onUpdate: function () {
            if (!this.fixed) {
                this.update();
            }
        },

        afterClose: function (opts) {
            // Remove overlay if exists and fancyBox is not opening
            // (e.g., it is not being open using afterClose callback)
            //if (this.overlay && !F.isActive) {
            if (this.overlay && !F.coming) {
                this.overlay.fadeOut(opts.speedOut, $.proxy(this.close, this));
            }
        }
    };

    /*
     *  Title helper
     */

    F.helpers.title = {
        defaults: {
            type: 'float', // 'float', 'inside', 'outside' or 'over',
            position: 'bottom' // 'top' or 'bottom'
        },

        beforeShow: function (opts) {
            var current = F.current,
                    text = current.title,
                    type = opts.type,
                    title,
                    target;

            if ($.isFunction(text)) {
                text = text.call(current.element, current);
            }

            if (!isString(text) || $.trim(text) === '') {
                return;
            }

            title = $('<div class="fancybox-title fancybox-title-' + type + '-wrap">' + text + '</div>');

            switch (type) {
                case 'inside':
                    target = F.skin;
                    break;

                case 'outside':
                    target = F.wrap;
                    break;

                case 'over':
                    target = F.inner;
                    break;

                default: // 'float'
                    target = F.skin;

                    title.appendTo('body');

                    if (IE) {
                        title.width(title.width());
                    }

                    title.wrapInner('<span class="child"></span>');

                    //Increase bottom margin so this title will also fit into viewport
                    F.current.margin[2] += Math.abs(getScalar(title.css('margin-bottom')));
                    break;
            }

            title[ (opts.position === 'top' ? 'prependTo' : 'appendTo') ](target);
        }
    };

    // jQuery plugin initialization
    $.fn.fancybox = function (options) {
        var index,
                that = $(this),
                selector = this.selector || '',
                run = function (e) {
                    var what = $(this).blur(), idx = index, relType, relVal;

                    if (!(e.ctrlKey || e.altKey || e.shiftKey || e.metaKey) && !what.is('.fancybox-wrap')) {
                        relType = options.groupAttr || 'data-fancybox-group';
                        relVal = what.attr(relType);

                        if (!relVal) {
                            relType = 'rel';
                            relVal = what.get(0)[ relType ];
                        }

                        if (relVal && relVal !== '' && relVal !== 'nofollow') {
                            what = selector.length ? $(selector) : that;
                            what = what.filter('[' + relType + '="' + relVal + '"]');
                            idx = what.index(this);
                        }

                        options.index = idx;

                        // Stop an event from bubbling if everything is fine
                        if (F.open(what, options) !== false) {
                            e.preventDefault();
                        }
                    }
                };

        options = options || {};
        index = options.index || 0;

        if (!selector || options.live === false) {
            that.unbind('click.fb-start').bind('click.fb-start', run);

        } else {
            D.undelegate(selector, 'click.fb-start').delegate(selector + ":not('.fancybox-item, .fancybox-nav')", 'click.fb-start', run);
        }

        this.filter('[data-fancybox-start=1]').trigger('click');

        return this;
    };

    // Tests that need a body at doc ready
    D.ready(function () {
        var w1, w2;

        if ($.scrollbarWidth === undefined) {
            // http://benalman.com/projects/jquery-misc-plugins/#scrollbarwidth
            $.scrollbarWidth = function () {
                var parent = $('<div style="width:50px;height:50px;overflow:auto"><div/></div>').appendTo('body'),
                        child = parent.children(),
                        width = child.innerWidth() - child.height(99).innerWidth();

                parent.remove();

                return width;
            };
        }

        if ($.support.fixedPosition === undefined) {
            $.support.fixedPosition = (function () {
                var elem = $('<div style="position:fixed;top:20px;"></div>').appendTo('body'),
                        fixed = (elem[0].offsetTop === 20 || elem[0].offsetTop === 15);

                elem.remove();

                return fixed;
            }());
        }

        $.extend(F.defaults, {
            scrollbarWidth: $.scrollbarWidth(),
            fixed: $.support.fixedPosition,
            parent: $('body')
        });

        //Get real width of page scroll-bar
        w1 = $(window).width();

        H.addClass('fancybox-lock-test');

        w2 = $(window).width();

        H.removeClass('fancybox-lock-test');

        $("<style type='text/css'>.fancybox-margin{margin-right:" + (w2 - w1) + "px;}</style>").appendTo("head");
    });

}(window, document, jQuery));