import { StyleSheet } from "react-native";
import colors from "@/constants/colors";
import fonts from "@/constants/font-family";

const styles = StyleSheet.create({
	outerContainer: {
		minWidth: '80%',
		width: 280,
		borderRadius: 28,
		shadowColor: colors.THEME_500,
		shadowOffset: {
			width: 0,
			height: 2,
		},
		shadowOpacity: 0.23,
		shadowRadius: 2.62,
	},
	innerContainer: {
		flex: 1,
		backgroundColor: colors.NEUTRAL_0,
		padding: 16,
		borderRadius: 28,
		gap: 16
	},
	header: {
		flexDirection: 'row',
		justifyContent: 'space-between'
	},
	headerLeft: {
		gap: 4,
		marginRight: 32
	},
	closeContainer: {
		position: 'absolute',
		right: 0,
		zIndex: 100
	},
	title: {
		fontFamily: fonts.MEDIUM,
		fontSize: 16,
		lineHeight: 24,
		letterSpacing: 0.15
	},
	category: {
		fontFamily: fonts.REGULAR,
		fontSize: 12,
		lineHeight: 16,
		color: colors.NEUTRAL_300
	},
	info: {
		gap: 4
	},
	infoItem: {
		flexDirection: 'row',
		gap: 4
	},
	infoText: {
		fontFamily: fonts.REGULAR,
		fontSize: 12,
		lineHeight: 16,
		marginRight: 8
	},
	standardInfoText: {
		color: colors.NEUTRAL_600
	},
	linkInfoText: {
		color: colors.INFO_100
	},
	tipContainer: {
		width: '100%',
		alignItems: 'center',
		paddingBottom: 8
	},
	tip: {
		width: 0,
		height: 0,
		backgroundColor: 'transparent',
		borderStyle: "solid",
		borderLeftWidth: 12,
		borderRightWidth: 12,
		borderTopWidth: 20,
		borderLeftColor: 'transparent',
		borderRightColor: 'transparent',
		borderTopColor: colors.NEUTRAL_0
	}
});

export default styles;