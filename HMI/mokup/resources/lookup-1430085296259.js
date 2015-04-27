(function(window, undefined) {
  var dictionary = {
    "f4396233-b86a-414b-9f22-11a175b18631": "company_student_search",
    "1ababba3-849a-428c-bdf1-fecc42c7bee2": "company_Offer_create",
    "9bb6822c-5d4f-491c-9bfe-f2148ed5709a": "company profile",
    "cd24bf32-a2cb-4bae-80e7-7fd85d1bd5a0": "student_Offers",
    "d99e6d60-970e-4f83-9c84-f4f4c3d6bf38": "company_Offers",
    "f23e8f89-bd35-4ddf-8f13-b078e8a98778": "company_home",
    "f21b1a96-d7d9-460e-adc9-61a8d37bf415": "student_Ofers_search",
    "c8779139-2d91-4911-9f04-b283ce755c06": "company_Offer Info",
    "c33f51f5-e968-4ecd-a296-3768d48f3b7d": "student_home_side_menu",
    "6fa59add-59f9-4860-82e0-3f74076f6a60": "company_home_side_menu",
    "12f95033-51c0-4a0b-bb64-c80c715ca252": "Error screen",
    "11b0c383-30fd-4ef1-a19c-9de34f9c0f3f": "student_companies_search",
    "57093960-5fed-42ca-9ca1-fdb3ea2a5a1a": "company_students",
    "193ae028-6f59-4f1e-bc7d-256dc234d694": "student_companies",
    "1432c19d-c891-4d09-9d9e-a0e9b91f9494": "login screen",
    "569fef1c-c8a8-4149-88ad-ae0f77e07844": "Offer Info",
    "6e2162cb-dabe-4230-b9d2-277de622c004": "student_info",
    "cadcdbd5-d639-4865-b840-0c142b724760": "student_home",
    "f39803f7-df02-4169-93eb-7547fb8c961a": "Template 1"
  };

  var uriRE = /^(\/#)?(screens|templates|masters)\/(.*)(\.html)?/;
  window.lookUpURL = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, url;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      url = folder + "/" + canvas;
    }
    return url;
  };

  window.lookUpName = function(fragment) {
    var matches = uriRE.exec(fragment || "") || [],
        folder = matches[2] || "",
        canvas = matches[3] || "",
        name, canvasName;
    if(dictionary.hasOwnProperty(canvas)) { /* search by name */
      canvasName = dictionary[canvas];
    }
    return canvasName;
  };
})(window);