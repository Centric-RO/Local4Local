import '@/gesture-handler';
import { useEffect } from "react";
import { Link, Stack } from "expo-router";
import { I18nextProvider, useTranslation } from "react-i18next";
import i18n from '../i18n';
import STACK_OPTIONS from "@/constants/stack-config";
import { useFonts } from "expo-font";
import { Roboto_500Medium, Roboto_400Regular } from '@expo-google-fonts/roboto'
import { IconButton, PaperProvider } from "react-native-paper";
import { JsStack } from "@/js-stack";
import AsyncStorage from "@react-native-async-storage/async-storage";
import styles from "@/__styles__/layout-style";
import paperIcons from "@/constants/paper-icons";
import React from 'react';

export default function RootLayout() {
	const { t } = useTranslation('common');
	const [loaded, error] = useFonts({
		Roboto_500Medium,
		Roboto_400Regular
	});

	useEffect(() => {
		const getPreferredLanguage = async () => {
			try {
				const value = await AsyncStorage.getItem('language');
				if (value !== null) {
					await i18n.changeLanguage(value);
				}
			} catch (e) {
				console.log(e);
			}
		}

		getPreferredLanguage();
	}, []);

	if (!loaded && !error) {
		return null;
	}

	return (
		<I18nextProvider i18n={i18n}>
			<PaperProvider>
				<JsStack screenOptions={STACK_OPTIONS}>
					<Stack.Screen name="index" options={{
						headerTitle: t('title.merchants'),
						headerRight: () => (
							<Link href="/change-language" asChild>
								<IconButton icon={paperIcons.WEB} style={styles.headerRightIcon} />
							</Link>
						)
					}} />
					<Stack.Screen name="change-language" options={{
						headerTitle: t('title.changeLanguage'),
					}} />
				</JsStack>
			</PaperProvider>
		</I18nextProvider>
	);
}
