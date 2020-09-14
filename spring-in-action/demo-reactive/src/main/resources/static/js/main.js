$(document).ready(() => {
    $.ajax({
        url: "/api/v1/product?pageNum=0&pageSize=4",
        type: "GET",
        success: (data) => {
            // console.log(data);
            kk(data);
        }
    });

    const category = getUrlParams().category;
    const url = (category !== undefined) ? "/api/v1/product?pageNum=0&pageSize=4&category="+getUrlParams().category : "/api/v1/product?pageNum=0&pageSize=4"

    $.ajax({
        url: url,
        type: "GET",
        success: (data) => {
            // console.log(data);
            ll(data);
        }
    });
});

function getUrlParams() {
    var params = {};
    window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(str, key, value) { params[key] = value; });
    return params;
}

const kk = (data) => {
    data.map(product => {
        return `
                <article class="card-top">
                    <a class="card-link" th:data-event-label="${product.no}" href="/product/${product.no}">
                        <div class="card-image">
                            <img alt="상품 이미지" src="${product.image}">
                        </div>
                    
                        <div class="card-description">
                            <h2 class="card-title">${product.name}</h2>
                    
                            <div class="card-region-name">${product.category}</div>
                    
                            <div class="card-price">${product.price}원</div>
                    
                            <div class="card-counts">
                                <span>관심 4</span>∙<span>채팅 50</span>
                            </div>
                        </div>
                    </a>
                </article>`;
    }).forEach(d => $("#popular-products .cards-wrap").eq(0).append(d));
};

const ll = (data) => {
   data.map(product => {
       return `
               <article class="card-top">
                   <a class="card-link" th:data-event-label="${product.no}" href="/product/${product.no}">
                       <div class="card-image">
                           <img alt="상품 이미지" src="${product.image}">
                       </div>

                       <div class="card-description">
                           <h2 class="card-title">${product.name}</h2>

                           <div class="card-region-name">${product.category}</div>

                           <div class="card-price">${product.price}원</div>

                           <div class="card-counts">
                               <span>관심 4</span>∙<span>채팅 50</span>
                           </div>
                       </div>
                   </a>
               </article>`;
   }).forEach(d => $("#category-products .cards-wrap").eq(0).append(d));
};