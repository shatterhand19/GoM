let search = function() {
    // get value from search bar
    let value = $("#search").val();
    let request = "/search/" + value;
    // FOR TESTING
    let data = buildTestData();
    displayResults(data);
    // $.get(request, function (data) {
    //     displayResults(JSON.parse(data));
    // });
};

function displayResults(data) {
    for (let img in data) {
        if (data.hasOwnProperty(img)) {
            displayImage(data[img]);
        }
    }
    setDisplay(data[0]);
}

function displayImage(img) {
    let $imgdiv = $("<div>");
    let $img = $("<img>");
    $img.attr('src', img);
    $img.appendTo($imgdiv);
    $imgdiv.addClass('meme');
    $imgdiv.hover(function () {
        setDisplay(img);
    });
    $imgdiv.appendTo('#memes_small');
}

function setDisplay(img) {
    let $img = $("<img>");
    $img.attr('src', img);
    let $helper = $("<span>");
    $helper.addClass("helper");

    let $display = $("#display");
    $display.empty();
    $display.append($helper);
    $display.append($img);
}

function buildTestData() {
    let data = [];
    for (let i = 1; i <= 10; i++) {
        data.push("test_img/1 (" + i + ").jpg");
    }
    return data;
}