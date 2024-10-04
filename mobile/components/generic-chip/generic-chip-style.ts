import { StyleSheet } from "react-native";
import colors from "@/constants/colors";
import fonts from "@/constants/font-family";

const styles = StyleSheet.create({
	selectedChip: {
		backgroundColor: colors.THEME_500,
	},
	commonText: {
		fontFamily: fonts.MEDIUM
	},
	selectedText: {
		color: colors.NEUTRAL_0,
	},
	defaultText: {
		color: colors.NEUTRAL_900,
	}
});

export default styles;