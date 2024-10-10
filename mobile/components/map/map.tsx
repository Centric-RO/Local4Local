import MapView, { Marker } from "react-native-maps";
import { MAP_STYLE, MARKER_IMAGES } from "@/constants/map-config";
import MerchantDto from "@/models/merchant-view-dto";
import styles from "@/components/map/map-style";
import MerchantCallout from "@/components/merchant-callout/merchant-callout";
import React, { useCallback, useRef } from "react";
import { useFocusEffect } from "expo-router";

export default function Map({ merchants }: MapProps) {
	const markerRefs = useRef<{ [key: string]: React.ElementRef<typeof Marker> | null }>({});

	useFocusEffect(useCallback(() => {
		return () => {
			closeAllCallouts();
		}
	}, []));

	const closeCallout = (kvk: string): void => {
		markerRefs.current[kvk]?.hideCallout();
	};

	const closeAllCallouts = (): void => {
		for (const kvk in markerRefs.current) {
			markerRefs.current[kvk]?.hideCallout();
		}
	};

	return (
		<>
			<MapView
				style={styles.map}
				customMapStyle={MAP_STYLE}
				showsCompass={false}
			>
				{merchants.map((merchant: MerchantDto) =>
					<Marker
						key={merchant.kvk}
						coordinate={{
							latitude: merchant.latitude,
							longitude: merchant.longitude
						}}
						image={MARKER_IMAGES[merchant.category.split('.')[1]]}
						ref={ref => {
							if (!ref) {
								delete markerRefs.current[merchant.kvk];
								return;
							}
							markerRefs.current[merchant.kvk] = ref;
						}}
						pointerEvents='auto'
					>
						<MerchantCallout merchant={merchant} onClose={() => closeCallout(merchant.kvk)} />
					</Marker>
				)}
			</MapView>
		</>
	);
}

interface MapProps {
	merchants: MerchantDto[]
}