let search = function() {
    // get value from search bar
    let value = $("#search").val();
    let request = "/search/" + value;
    // FOR TESTING
    // let data = buildTestData();
    // displayResults(data);
    $.ajax(request, {
        dataType: "text",
        success: function(data) {
            console.log(data);
            let json = JSON.parse(data);
            if (json.length > 0) {
                displayResults(json);
            } else {
                displayNoResults();
            }
        },
        error: function () {
            displayNoResults();
        }
    })
};

function displayNoResults() {
    $("#memes_small").empty();
    let $display = $("#display");
    $display.empty();

    let $noresults = $('<p>No results...</p>');
    $display.append($noresults);
}

function displayResults(data) {
    $("#memes_small").empty();
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
    $img.attr('src', img.path);
    $img.appendTo($imgdiv);
    $imgdiv.addClass('meme');
    $imgdiv.click(function () {
        setDisplay(img);
    });
    $imgdiv.appendTo('#memes_small');
}

function setDisplay(img) {
    let $img = $("<img>");
    $img.attr('src', img.path);
    let $helper = $("<span>");
    $helper.addClass("helper");
    let $list = buildKeywordsList(img);

    let $display = $("#display");
    $display.empty();
    $display.append($helper);
    $display.append($img);
    $display.append($list);
}

function buildKeywordsList(img) {
    let $display = $("<ul>");
    $display.addClass("keywords");
    for (let word in img.keywords) {
        if (img.keywords.hasOwnProperty(word)) {
            let $span = $("<span>" + img.keywords[word] + "</span>");
            let $li = $("<li>");
            $li.attr("path", img.path);
            $li.attr("keyword", img.keywords[word]);
            $li.attr("id", word);
            let $upvote = $("<input type='button' value='+' class='vote_button'>");
            let $downvote = $("<input type='button' value='-' class='vote_button'>");
            $upvote.attr("onclick", "upvote(\"" + img.path + "\", \"" + img.keywords[word] + "\")");
            $downvote.attr("onclick", "downvote(\"" + img.path + "\", \"" + img.keywords[word] + "\")");
            $li.append($upvote);
            $li.append($downvote);
            $li.append($span);
            $display.append($li);
        }
    }

    // Add input
    let $input_li = $("<li>");
    let $input = $("<input type='text' placeholder='new keyword...'>");
    let $submit = $("<input type='button' value='submit'>");
    $submit.attr("onclick", "upvote(\"" + img.path + "\", \"" + $input.val() + "\")");

    $input_li.append($input);
    $input_li.append($submit);
    $display.append($input_li);
    return $display;
}

function upvote(imgPath, keyword) {
    $.ajax({
        url: imgPath + ':' + keyword + ':' + 'up',
        type: 'POST',
        dataType: 'text',
        success: function () {
            alert("Successfully voted for " + keyword);
        },
        error: function () {
            alert("Unable to vote for " + keyword);
        }
    });
}

function downvote(imgPath, keyword) {
    $.ajax({
        url: imgPath + ':' + keyword + ':' + 'down',
        type: 'POST',
        success: function () {
            alert("Successfully voted for " + keyword);
        },
        error: function () {
            alert("Unable to vote for " + keyword);
        }
    });
}


function buildTestData() {
    let data = [];
    for (let i = 1; i <= 10; i++) {
        data.push({ path: "test_img/1 (" + i + ").jpg", keywords: [ "example", "other example" ]});
    }
    return data;
}