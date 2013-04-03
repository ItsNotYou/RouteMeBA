package net.sf.gilead.pojo.actionscript
{	
    [RemoteClass(alias="net.sf.gilead.pojo.java5.LightEntity")]
	public class LightEntity
	{
		/**
		 * The internal proxy informations
		 * (a map of <String, Map<String, Serializable>>)
		 */
		public var proxyInformations:Object;
		
		/**
	 	 * Map of persistence initialisation state.
	 	 * (Map<String,Boolean>
	 	 */
		public var initializationMap:Object;
		
		/**
		 * Constructor of Lazy Pojo abstract class.
		 **/
		public function LightEntity()
		{
			proxyInformations = new Object();
			initializationMap = new Object();
		}
		
		
		/**
		 * Indicates if the property was initialized on server side
		 */ 
		public function isInitialized(property:String):Boolean
		{
			return initializationMap[property];
		}
		
		/**
		 * Indicates that the property has been initialized on client side
		 */ 
		public function setInitialized(property:String):void
		{
			initializationMap[property] = true;
		}
		
		/**
		 * toString overrides
		 */
		public function debugString():String
		{
			var result:String;
			result = "[proxyInformations : ";
			result += proxyInformations + " (";
			for(var key:String in proxyInformations)
			{
				result += key + ":" + proxyInformations[key] + ","; 
			}
			result += ")], [initializationMap : "; 
			result += initializationMap + " (";
			for(var key2:String in initializationMap)
			{
				result += key2 + ":" + initializationMap[key2] + ","; 
			}
			
			result += ")]";
			return result;
		}
	}
}