function search() {
    // get value from search bar
    let value = $("#search").val();
    let request = "/search/" + value;

    $.ajax(request, {
        dataType: "text",
        success: function(data) {
            console.log(data);
            $("#content").animate({ backgroundColor: "white" }, "slow");
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
    });
}

function displayNoResults() {
    $("#memes_small").empty();
    let $display = $("#display");
    $display.empty();

    let $noresults = $('<p>No results...</p>');
    $display.append($noresults);
}

function displayResults(data) {
    $("#memes_small").empty();
    let count = 0;
    for (let img in data) {
        if (data.hasOwnProperty(img)) {
            data[img].id = "img_" + count;
            displayImage(data[img]);
            count++;
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
    $img.attr('onclick', "copyDisplay()");
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
    let $display = $("<div>");
    $display.addClass("keywords_display");

    let $title = $("<h4>Vote for keywords</h4>");
    $title.addClass("title");

    let $list = $("<ul>");
    $list.addClass("keywords");
    let count = 0;
    for (let word in img.keywords) {
        if (img.keywords.hasOwnProperty(word)) {
            let $span = $("<span>" + img.keywords[word] + "</span>");
            $span.addClass("keyword");
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
            $list.append($li);
            count++;
        }
    }

    // Add input
    let $input_li = $("<div>");
    let $input = $("<input id=" + img.id + " type='text' placeholder='new keyword...'>");
    let $submit = $("<input type='button' value='submit'>");
    $submit.attr("onclick", "upvote(\"" + img.path + "\", $(\"#" + img.id + "\").val())");

    $input_li.append($input);
    $input_li.append($submit);

    let $list_div = $("<div>");
    $list_div.attr("id", "keywords_div");
    $list_div.append($list);

    $display.append($title);
    $display.append($list_div);
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

function copyDisplay() {
    let display = $("#display img");
    display.select();
    document.execCommand("copy");
    document.selection.empty();
}