var Map = function(){
  this.mpData = {};
 };
 
 Map.prototype = {
  size : function(){
   return this.getKeys().length;
//   return Object.keys(this.dict).length;
  },
  isEmpty : function(){
   return this.getKeys().length == 0;
//   return Object.keys(this.dict).length == 0;
  },
  get : function(key){
   return this.mpData[key];
  },
  containsKey : function(key){
   if( this.get(key) !== undefined) {
    return true;
   } else {
    return false;
   }
  },
  put : function(key, value){
   this.mpData[key] = value;
  },
  remove : function(key){
   'use strict';
   delete this.mpData[key]; 
  },
  clear : function(){
   this.mpData = {};
  },
  forEach : function(callback){
   for(var key in this.mpData){
    callback(this.get(key));
   }
//   var len = this.size();
//   for (i = 0; i < len; i++) {
//    var item = this.get( Object.keys(this.dict)[i] );
//    callback(item);
//   } 
  },
  getKeys : function(){
   var keys = new Array();
   for(var key in this.mpData){
    keys.push(key);
   }
   
   return keys;
//   return Object.keys(this.dict);
  },
  getValues : function(){
   var values = new Array();
   for(var key in this.mpData){
    values.push(this.get(key));
   }
   
   return values;  
  },
  getMpData : function(){
   return this.mpData;
  }
 };