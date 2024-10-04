import { ImageURISource } from "react-native";

export const MAP_STYLE = [
	{
		"featureType": "poi",
		"elementType": "labels.icon",
		"stylers": [
			{
				"saturation": -50
			},
			{
				"lightness": 40
			}
		]
	},
	{
		"featureType": "poi",
		"elementType": "labels.text",
		"stylers": [
			{
				"saturation": -50
			},
			{
				"lightness": 40
			}
		]
	},
	{
		"featureType": "transit",
		"elementType": "labels.icon",
		"stylers": [
			{
				"saturation": -50
			},
			{
				"lightness": 40
			}
		]
	}
];

export const MARKER_IMAGES: { [index: string]: ImageURISource } = {
	foodAndBeverage: require('@/assets/markers/foodAndBeverage.png'),
	retail: require('@/assets/markers/retail.png'),
	healthAndWellness: require('@/assets/markers/healthAndWellness.png'),
	services: require('@/assets/markers/services.png'),
	entertainmentAndLeisure: require('@/assets/markers/entertainmentAndLeisure.png'),
	specialtyStores: require('@/assets/markers/specialtyStores.png'),
	technologyAndCommunications: require('@/assets/markers/technologyAndCommunications.png'),
	homeImprovement: require('@/assets/markers/homeImprovement.png'),
	other: require('@/assets/markers/other.png')
};
