import { View, Text, Pressable, Platform } from "react-native";
import styles from "@/components/merchant-callout/merchant-callout-style";
import { Icon } from "react-native-paper";
import { Callout } from "react-native-maps";
import colors from "@/constants/colors";
import MerchantDto from "@/models/merchant-view-dto";
import { useTranslation } from "react-i18next";
import { Href, Link } from "expo-router";
import paperIcons from "@/constants/paper-icons";

export default function MerchantCallout({ merchant, onClose }: MerchantCalloutProps) {
	const { t } = useTranslation('common');

	return (
		<Callout
			tooltip={true}
			style={styles.outerContainer}
			onPress={() => {
				if (Platform.OS !== 'android') {
					return;
				}
				onClose();
			}}
		>
			<View style={styles.innerContainer}>
				<View style={styles.header}>
					<View style={styles.headerLeft}>
						<Text style={styles.title}>{merchant.companyName}</Text>
						<Text style={styles.category}>{t(merchant.category)}</Text>
					</View>
					<View style={styles.closeContainer}>
						<Pressable onPress={onClose}>
							<Icon source={paperIcons.CLOSE} size={24} />
						</Pressable>
					</View>
				</View>
				<View style={styles.info}>
					<View style={styles.infoItem}>
						<Icon source={paperIcons.MAP_MARKER} size={16} color={colors.NEUTRAL_400} />
						<Text style={[styles.infoText, styles.standardInfoText]}>{merchant.address}</Text>
					</View>
					{merchant.website && (
						<View style={styles.infoItem}>
							<Icon source={paperIcons.WEB} size={16} color={colors.NEUTRAL_400} />
							{Platform.OS !== 'android' ? (
								<Link href={merchant.website as Href<string | object>}>
									<Text style={[styles.infoText, styles.linkInfoText]}>{merchant.website}</Text>
								</Link>
							) : (
								<Text style={[styles.infoText, styles.standardInfoText]}>{merchant.website}</Text>
							)}
						</View>
					)}
				</View>
			</View>
			{Platform.OS === 'android' && (
				<View style={styles.tipContainer}>
					<View style={styles.tip} />
				</View>
			)}
		</Callout>
	);
}

interface MerchantCalloutProps {
	merchant: MerchantDto,
	onClose: () => void
}